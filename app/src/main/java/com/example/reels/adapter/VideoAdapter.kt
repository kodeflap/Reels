package com.example.reels.adapter

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.reels.R
import com.example.reels.model.VideoModel
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions


class VideoAdapter(options: FirebaseRecyclerOptions<VideoModel?>) :
    FirebaseRecyclerAdapter<VideoModel?, VideoAdapter.MyViewHolder?>(options) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.video_layout,
            parent,
            false
        )
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int, model: VideoModel) {
        holder.setData(model,holder.itemView.context)
    }

    //myviewholder
    inner class MyViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        lateinit var videoView: VideoView
        lateinit var title: TextView
        lateinit var desc: TextView
        lateinit var progressBar: ProgressBar
        lateinit var favourite: ImageView
        lateinit var download: ImageView
        var isFav = false
        var isDownloaded: Long = 0


        fun setData(obj: VideoModel,context: Context) {
            videoView.setVideoPath(obj.url)
            title.text = obj.title
            desc.text = obj.desc
            videoView.setOnPreparedListener { mediaPlayer ->
                progressBar.visibility = View.GONE
                mediaPlayer.start()
            }
            videoView.setOnCompletionListener { mediaPlayer -> mediaPlayer.start() }
            favourite.setOnClickListener {
                if (!false) {
                    favourite.setImageResource(R.drawable.fav_fill)
                    favourite.setColorFilter(ContextCompat.getColor(context,R.color.red))
                    isFav = true
                } else {
                    favourite.setImageResource(R.drawable.fav_border)
                    isFav = false
                }
            }
            /** download **/
            download.setOnClickListener {
                //set download file
                var request = DownloadManager.Request(Uri.parse(obj.url))
                    .setTitle(obj.title + ".mp4")
                    .setDescription("${obj.title} is downloading......")
                    .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE)
                    .setAllowedOverMetered(true)
                    .setVisibleInDownloadsUi(false)
                request.setDestinationInExternalPublicDir(
                    Environment.DIRECTORY_DOWNLOADS,
                    "fileName.mp4"
                )
                request.allowScanningByMediaScanner()
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)

                var dm =
                    itemView.context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager

                isDownloaded = dm.enqueue(request)
                //set icon
                download.setImageResource(R.drawable.download_sucess)
                download.setColorFilter(ContextCompat.getColor(context,R.color.red))

                request.setDestinationInExternalFilesDir(
                    itemView.context.applicationContext,
                    "/file",
                    "Question1.mp4"
                )
            }

            var br = object : BroadcastReceiver() {
                override fun onReceive(context: Context?, intent: Intent?) {
                    var id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
                    if (id == isDownloaded) {
                        Toast.makeText(itemView.context,"${obj.title} is downloaded",Toast.LENGTH_LONG).show()
                    }
                }
            }

            itemView.context.registerReceiver(br, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))

        }

        init {
            videoView = v.findViewById(R.id.video_view)
            title = v.findViewById(R.id.video_title)
            desc = v.findViewById(R.id.video_description);
            progressBar = v.findViewById(R.id.video_progress_bar)
            favourite = v.findViewById(R.id.fav)
            download = v.findViewById(R.id.dowload)
        }
    }

}