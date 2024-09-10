package com.example.studentdashboard.adapters

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.studentdashboard.databinding.ContentItemBinding
import com.example.studentdashboard.models.PdfFile

open class ContentAdapter(
    private val context: Context,
    private val list: ArrayList<PdfFile>,
) : RecyclerView.Adapter<ContentAdapter.MyViewHolder>() {

    inner class MyViewHolder(val binding: ContentItemBinding) : RecyclerView.ViewHolder(binding.root) {
        val tvPdfName = binding.tvPdfName
        val btnDownload = binding.btnDownload
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ContentItemBinding.inflate(LayoutInflater.from(/*parent.context*/context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val model = list[position]

        holder.tvPdfName.text = model.name
        holder.btnDownload.setOnClickListener {
            downloadPdf(model)
        }
    }

    override fun getItemCount(): Int = list.size

    // Function to download the PDF
    private fun downloadPdf(pdfFile: PdfFile) {
        val request = DownloadManager.Request(Uri.parse(pdfFile.url))
            .setTitle(pdfFile.name)  // Title of the Download Notification
            .setDescription("Downloading ${pdfFile.name}")  // Description of the Download Notification
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)  // Show Notification when download completes
            .setAllowedOverMetered(true)  // Allow download over mobile data
            .setAllowedOverRoaming(true)  // Allow download over roaming
            .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, pdfFile.name)  // Set the destination path

        val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        downloadManager.enqueue(request)  // Enqueue the download request
    }
}