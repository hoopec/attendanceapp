package com.example.attendanceapp.ui

import android.app.AlertDialog
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.attendanceapp.adapter.MemberAdapter
import com.example.attendanceapp.data.AppDatabase
import com.example.attendanceapp.databinding.ActivityMemberManageBinding
import com.example.attendanceapp.model.Group
import com.example.attendanceapp.model.Member

class MemberManageActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMemberManageBinding
    private lateinit var database: AppDatabase
    private lateinit var memberAdapter: MemberAdapter

    private var currentGroup: Group? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMemberManageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        database = AppDatabase.getInstance(this)

        val groupId = intent.getStringExtra("group_id")
        currentGroup = groupId?.let { database.getGroup(it) }

        if (currentGroup == null) {
            Toast.makeText(this, "小组不存在", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        setupToolbar()
        setupRecyclerView()
        loadMembers()
    }

    private fun setupToolbar() {
        binding.toolbar.title = "${currentGroup?.name} - 成员管理"
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }

        // 添加菜单选项
        binding.toolbar.inflateMenu(com.example.attendanceapp.R.menu.menu_member_manage)
        binding.toolbar.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                com.example.attendanceapp.R.id.action_add_member -> {
                    showAddMemberDialog()
                    true
                }
                com.example.attendanceapp.R.id.action_reset_members -> {
                    showResetMembersConfirmDialog()
                    true
                }
                else -> false
            }
        }
    }

    private fun setupRecyclerView() {
        memberAdapter = MemberAdapter(
            mutableListOf(),
            onMemberClick = { member ->
                showEditMemberDialog(member)
            },
            onMemberLongClick = { member ->
                showDeleteMemberConfirmDialog(member)
            },
            showEditButton = true  // 在成员管理界面显示编辑按钮
        )

        binding.rvMembers.apply {
            layoutManager = LinearLayoutManager(this@MemberManageActivity)
            adapter = memberAdapter
        }
    }

    private fun loadMembers() {
        currentGroup?.let { group ->
            if (group.members.isEmpty()) {
                binding.tvEmpty.visibility = View.VISIBLE
                binding.rvMembers.visibility = View.GONE
            } else {
                binding.tvEmpty.visibility = View.GONE
                binding.rvMembers.visibility = View.VISIBLE
                memberAdapter.updateMembers(group.members)
            }
        }
    }

    private fun showAddMemberDialog() {
        val input = EditText(this)
        input.hint = "输入成员姓名"
        input.setPadding(50, 30, 50, 30)

        AlertDialog.Builder(this)
            .setTitle("添加成员")
            .setView(input)
            .setPositiveButton("添加") { _, _ ->
                val name = input.text.toString().trim()
                if (name.isNotEmpty()) {
                    val member = Member(name = name)
                    currentGroup?.addMember(member)
                    saveGroup()
                    loadMembers()
                    Toast.makeText(this, "添加成功", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "姓名不能为空", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("取消", null)
            .show()
    }

    private fun showEditMemberDialog(member: Member) {
        val input = EditText(this)
        input.setText(member.name)
        input.hint = "输入新姓名"
        input.setPadding(50, 30, 50, 30)
        input.selectAll()

        AlertDialog.Builder(this)
            .setTitle("编辑成员")
            .setView(input)
            .setPositiveButton("保存") { _, _ ->
                val newName = input.text.toString().trim()
                if (newName.isNotEmpty()) {
                    val updatedMember = member.copy(name = newName)
                    currentGroup?.updateMember(updatedMember)
                    saveGroup()
                    loadMembers()
                    Toast.makeText(this, "修改成功", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "姓名不能为空", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("取消", null)
            .show()
    }

    private fun showDeleteMemberConfirmDialog(member: Member) {
        AlertDialog.Builder(this)
            .setTitle("删除成员")
            .setMessage("确定要删除 ${member.name} 吗？")
            .setPositiveButton("删除") { _, _ ->
                currentGroup?.removeMember(member.id)
                saveGroup()
                loadMembers()
                Toast.makeText(this, "已删除", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("取消", null)
            .show()
    }

    private fun showResetMembersConfirmDialog() {
        AlertDialog.Builder(this)
            .setTitle("重置成员")
            .setMessage("确定要删除该小组的所有成员吗？")
            .setPositiveButton("重置") { _, _ ->
                currentGroup?.members?.clear()
                saveGroup()
                loadMembers()
                Toast.makeText(this, "已重置", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("取消", null)
            .show()
    }

    private fun saveGroup() {
        currentGroup?.let {
            database.updateGroup(it)
        }
    }
}