package com.example.working.userfagment

import android.annotation.SuppressLint
import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.activity.addCallback
import androidx.annotation.RequiresApi
import androidx.core.net.toUri
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import coil.ImageLoader
import coil.request.ImageRequest
import coil.request.SuccessResult
import com.example.working.MyViewModel
import com.example.working.R
import com.example.working.adminui.viewmodel.AdminViewModel
import com.example.working.databinding.ViewFileFragmentBinding
import com.example.working.loginorsignup.TAG
import com.example.working.room.UserData
import com.example.working.utils.*
import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.io.File
import javax.inject.Inject


@AndroidEntryPoint
class ViewFileFragment : Fragment(R.layout.view_file_fragment) {
    private lateinit var binding: ViewFileFragmentBinding
    private val args: ViewFileFragmentArgs by navArgs()
    private val myViewModel: MyViewModel by activityViewModels()
    private val adminViewModel: AdminViewModel by activityViewModels()

    @Inject
    lateinit var customProgress: CustomProgress

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = ViewFileFragmentBinding.bind(view)
        if(args.fileinfo.localDownloadUrl!=null)
        offlineSee()
        else
        onlineSee(savedInstanceState)
        binding.myRoot.setOnRefreshListener {
            if (isWebsiteFile(args.fileinfo.fileName!!)) {
                checkConnection()
            } else
                binding.myRoot.isRefreshing = false
        }
        setHasOptionsMenu(true)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun onlineSee(savedInstanceState: Bundle?) {
        if (savedInstanceState != null && isWebsiteFile(args.fileinfo.fileName!!)) {
            binding.webView.restoreState(savedInstanceState)
            webViewLoading()
            onBackPressed()
        } else if (savedInstanceState == null && isWebsiteFile(args.fileinfo.fileName!!)) {
            binding.myRoot.isVisible = true
            checkConnection()
            webViewLoading()
            onBackPressed()
        } else if (isPngFile(args.fileinfo.fileName!!) || isJpgFile(args.fileinfo.fileName!!)) {
            binding.MyZoomImg.isVisible = true
            try {
                val uri = myViewModel.downloadFile.getValue(args.title)
                binding.MyZoomImg.setImageURI(uri)
            } catch (e: NoSuchElementException) {
                setImage()
            }
        } else if (isPdfFile(args.title)) {
            try {
                binding.showPDf.isVisible = true
                val str = myViewModel.downloadFile.getValue(args.title)
                Log.i(TAG, "onViewCreated: Uri->$str")
                showPdf(str)
            } catch (e: NoSuchElementException) {
                val id = setFileDownload()
                setBroadcastReceiver(id)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun offlineSee() {
        if (isPdfFile(args.title)){
            binding.showPDf.isVisible = true
            showPdf(args.fileinfo.localDownloadUrl?.toUri())
        }
        else if (isPngFile(args.title)|| isJpgFile(args.title)){
            binding.MyZoomImg.isVisible = true
            binding.MyZoomImg.setImageURI(args.fileinfo.localDownloadUrl?.toUri())
            if (binding.MyZoomImg.drawable==null)
                setImage()
        }
    }

    private fun setBroadcastReceiver(TrueId: Long) {
        val receiver = object : BroadcastReceiver() {
            @RequiresApi(Build.VERSION_CODES.M)
            override fun onReceive(context: Context?, intent: Intent?) {
                val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
                hideLoading()
                if (id == TrueId) {
                    binding.myRoot.isRefreshing = true
                    val uri = getFileUrl(getFileDir(fileName = args.title, requireContext()))
                    activity?.let {
                        Toast.makeText(it, "Downloaded Path \n $uri", Toast.LENGTH_LONG).show()
                    }
                    //Show PDf
                    if (showPdf(uri)) {
                        saveDownload(uri)
                        myViewModel.downloadFile[args.title] = uri!!
                    }
                } else
                    dialog(message = "File is Not Downloaded :(")
            }
        }
        activity?.registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))
    }

    @SuppressLint("SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.M)
    private fun showPdf(Uri: Uri?): Boolean {
        Uri?.let { uri ->
            binding.myRoot.isRefreshing = false
            binding.showPDf.isVisible = true
            binding.showPDf.fromUri(uri).defaultPage(0)
                .enableSwipe(true)
                .enableDoubletap(true)
                .scrollHandle(object : DefaultScrollHandle(activity) {})
                .onRender { _, _, _ ->
                    binding.showPDf.fitToWidth()
                }
                .spacing(2)
                .enableAnnotationRendering(true)
                .load()
            Log.i(TAG, "showPdf: ${binding.showPDf.currentPage}")
            return true
        }
        return false
    }

    private fun setFileDownload(): Long {
        val uri = getFileDir(args.title, requireContext())
        val request = getDownloadRequest(args.fileinfo, uri)
        showLoading("PDF is Downloading,\nDon't close the App.")
        val downloadManger: DownloadManager =
            activity?.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        return downloadManger.enqueue(request)
    }

    private fun getFileUrl(file: File)= getFileUrl(file,requireContext())

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.share_menu, menu)
        val share = menu.findItem(R.id.shareFile)
        share?.setOnMenuItemClickListener {
            if (isWebsiteFile(args.title)) {
                shareText(SHARED_WEBSITE)
            } else if (isPngFile(args.title) || isJpgFile(args.title)) {
                shareImage()
            } else if (isPdfFile(args.title)) {
                shareText(SHARE_PDF)
            }
            return@setOnMenuItemClickListener true
        }
        super.onCreateOptionsMenu(menu, inflater)
    }

    private fun shareText(SHARED: String) {
        activity?.let {
            shareText(
                SHARED = SHARED,
                context = it,
                downloadUri = args.fileinfo.downloadUrl!!,
                sharedBy = args.fileinfo.sourceId!!
            )
        }
    }

    private fun shareImage() {
        try {
            val uri = myViewModel.downloadFile.getValue(args.title)
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "image/*"
                putExtra(Intent.EXTRA_STREAM, uri)
                putExtra(Intent.EXTRA_TEXT, "$SHARE_IMAGE \n\nShared By :${args.fileinfo.sourceId}")
            }
            startActivity(Intent.createChooser(intent, "Share File!"))
        } catch (e: Exception) {
            dialog(message = e.localizedMessage ?: "")
        }

    }

    private fun setImage() {
        showLoading("Image is Loading")
        lifecycleScope.launch {
            getBitmap()?.let { bitMap ->
                bitUrl(bitMap)?.let { uri ->
                    hideLoading()
                    saveDownload(uri)
                    binding.MyZoomImg.setImageURI(uri)
                    myViewModel.downloadFile[args.title] = uri
                }
            }
        }
    }

    private fun saveDownload(uri: Uri?) {
        val fileInfo = args.fileinfo
        fileInfo.localDownloadUrl = uri.toString()
        adminViewModel.saveDownload(UserData(fileInfo = fileInfo, date = fileInfo.date!!))
            .observe(viewLifecycleOwner) {
                if (it is MySealed.Success) {
                    dialog(title = "Success!", message = it.data!!)
                }
            }
    }

    private fun bitUrl(bitmap: Bitmap): Uri? {
        val byteArray = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArray)
        val path = MediaStore.Images.Media.insertImage(
            activity?.contentResolver,
            bitmap,
            "IMG_" + System.currentTimeMillis(),
            null
        )
        return Uri.parse(path)
    }

    private suspend fun getBitmap(): Bitmap? {
        val loading = ImageLoader(requireContext())
        val request = ImageRequest.Builder(requireContext())
            .data(args.fileinfo.downloadUrl)
            .build()
        return try {
            val result = (loading.execute(request) as SuccessResult).drawable
            (result as BitmapDrawable).bitmap
        } catch (e: Exception) {
            hideLoading()
            dialog(message = e.localizedMessage!!)
            null
        }
    }

    private fun dialog(title: String = "Error", message: String) {
        activity?.let {
            val action = ViewFileFragmentDirections.actionGlobalPasswordDialog2(title, message)
            findNavController().navigate(action)
        }
    }

    private fun webViewLoading() {
        binding.webView.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView, newProgress: Int) {
                Log.i(TAG, "onProgressChanged: the Loading is $newProgress")
                if (newProgress == 100) {
                    Log.i(TAG, "onProgressChanged:Loading Completed")
                    hideLoading()
                    myViewModel.websiteLoading = false
                    binding.myRoot.isRefreshing = false
                } else if (newProgress <= 10) {
                    if (myViewModel.websiteLoading)
                        showLoading()
                    binding.myRoot.isRefreshing = true
                }
                super.onProgressChanged(view, newProgress)
            }
        }
    }

    private fun onBackPressed() {
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            Log.i(TAG, "onbackPressed: BackPressed called ")
            if (binding.webView.canGoBack()) {
                Log.i(TAG, "onbackPressed: Website can go back")
                binding.webView.goBack()
            } else {
                Log.i(TAG, "onbackPressed: website cannot  go back")
                if (!myViewModel.websiteLoading) {
                    myViewModel.websiteLoading = true
                    findNavController().popBackStack()
                }
            }
        }.handleOnBackPressed()
    }

    private fun showLoading(title: String = "Website is Loading") {
        activity?.let {
            customProgress.showLoading(it, title)
            return
        }
        Log.i(TAG, "hideLoading: iS NOT return")
        myViewModel.websiteLoading = true
    }

    private fun hideLoading() {
        activity?.let {
            customProgress.hideLoading(it)
            return
        }
        Log.i(TAG, "hideLoading: iS NOT return")
        myViewModel.websiteLoading = true
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun setData() {
        binding.webView.apply {
            webViewClient = WebViewClient()
            loadUrl(args.fileinfo.downloadUrl!!)
        }
        val webSettings: WebSettings = binding.webView.settings
        webSettings.javaScriptEnabled = true
        webSettings.loadWithOverviewMode = true
        webSettings.useWideViewPort = true
        webSettings.domStorageEnabled = true
        webSettings.loadsImagesAutomatically = true
    }

    private fun checkConnection() {
        val connectivityManager =
            activity?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val wifi = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
        val mobileNetwork = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)
        when {
            wifi!!.isConnected -> {
                setData()
                binding.webView.isVisible = true
            }
            mobileNetwork!!.isConnected -> {
                setData()
                binding.webView.isVisible = true
            }
            else -> {
                binding.webView.isVisible = false
                dialog(message = "Check your Internet Connection")
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        binding.webView.saveState(outState)
    }
}