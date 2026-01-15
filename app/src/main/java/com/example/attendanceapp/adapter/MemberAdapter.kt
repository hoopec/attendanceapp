package com.example.attendanceapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.attendanceapp.R
import com.example.attendanceapp.model.Member

class MemberAdapter(
    private val members: MutableList<Member>,
    private val onMemberClick: (Member) -> Unit,
    private val onMemberLongClick: ((Member) -> Unit)? = null,
    private val showEditButton: Boolean = false  // 是否显示编辑按钮
) : RecyclerView.Adapter<MemberAdapter.MemberViewHolder>() {

    inner class MemberViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameText: TextView = itemView.findViewById(R.id.tv_member_name)
        val statusText: TextView = itemView.findViewById(R.id.tv_member_status)
        val editButton: ImageButton = itemView.findViewById(R.id.btn_edit)

        fun bind(member: Member) {
            nameText.text = member.name

            // 根据模式显示状态或编辑按钮
            if (showEditButton) {
                statusText.visibility = View.GONE
                editButton.visibility = View.VISIBLE

                editButton.setOnClickListener {
                    onMemberClick(member)
                }

                // 在编辑模式下，点击整个item也触发编辑
                itemView.setOnClickListener {
                    onMemberClick(member)
                }
            } else {
                statusText.visibility = View.VISIBLE
                editButton.visibility = View.GONE

                statusText.text = member.status.displayName

                // 设置状态颜色
                val color = ContextCompat.getColor(itemView.context, member.status.colorRes)
                statusText.setTextColor(color)

                itemView.setOnClickListener {
                    onMemberClick(member)
                }
            }

            onMemberLongClick?.let { callback ->
                itemView.setOnLongClickListener {
                    callback(member)
                    true
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MemberViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_member, parent, false)
        return MemberViewHolder(view)
    }

    override fun onBindViewHolder(holder: MemberViewHolder, position: Int) {
        holder.bind(members[position])
    }

    override fun getItemCount(): Int = members.size

    fun updateMembers(newMembers: List<Member>) {
        members.clear()
        members.addAll(newMembers)
        notifyDataSetChanged()
    }

    fun removeMember(position: Int) {
        if (position in members.indices) {
            members.removeAt(position)
            notifyItemRemoved(position)
        }
    }
}