package com.covidproject.covid_respiratorycare.ui.main

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.covidproject.covid_respiratorycare.R
import com.covidproject.covid_respiratorycare.databinding.ItemNaverNewsBinding
import com.covidproject.covid_respiratorycare.ui.Service.main.NaverNews
import java.util.*

class MainNaverNewsAdapter(navernews : List<NaverNews>)
    : RecyclerView.Adapter<MainNaverNewsAdapter.ViewHolder>() {

    private var news: List<NaverNews> = navernews

    // 외부에서 연결해 사용할 클릭 인터페이스 생성
    private var listener : OnClickInterface? = null
    interface OnClickInterface{
        fun onItemClick(v:View, news: NaverNews, pos:Int)
    }

    fun setOnItemClickListener(listener : OnClickInterface){
        this.listener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, i: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_naver_news, parent, false)
//        val binding = ItemNaverNewsBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return news.size
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.bind(news[position])
    }

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        private val titleTv = itemView.findViewById<TextView>(R.id.item_navernews_title_tv)
        private val contentTv = itemView.findViewById<TextView>(R.id.item_navernews_content_tv)
        private val pubdataTv = itemView.findViewById<TextView>(R.id.item_navernews_pubdate_tv)

        // HTMl 마크업 태그 없애는 정규 식
        val re = "<(/)?([a-zA-Z]*)(\\s[a-zA-Z]*=[^>]*)?(\\s)*(/)?>".toRegex()

        fun bind(news: NaverNews) {
            // 태그와 따음표 바꿔주기
            var text: String = news.title.replace(re, "").replace("&quot;","\"")
            titleTv.setText(text)
            text = news.description.replace(re, "").replace("&quot;","\"")
            contentTv.setText(text)
            text = news.pubDate.replace(re, "").replace("&quot;","\"")
            pubdataTv.setText(text)

            itemView.setOnClickListener {
                listener?.onItemClick(itemView,news,position)
            }

        }
    }

}