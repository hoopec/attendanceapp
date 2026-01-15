package com.example.attendanceapp.ui

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.attendanceapp.R
import com.example.attendanceapp.adapter.MemberAdapter
import com.example.attendanceapp.data.AppDatabase
import com.example.attendanceapp.databinding.ActivityMainBinding
import com.example.attendanceapp.model.Group
import com.example.attendanceapp.model.Member

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var database: AppDatabase
    private lateinit var memberAdapter: MemberAdapter

    private var currentGroup: Group? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            binding = ActivityMainBinding.inflate(layoutInflater)
            setContentView(binding.root)

            database = AppDatabase.getInstance(this)

            setupRecyclerView()
            setupButtons()
            loadCurrentGroup()
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "初始化失败: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    override fun onResume() {
        super.onResume()
        loadCurrentGroup()
    }

    private fun setupRecyclerView() {
        memberAdapter = MemberAdapter(
            mutableListOf(),
            onMemberClick = { member ->
                toggleMemberStatus(member)
            },
            onMemberLongClick = { member ->
                showMemberOptionsDialog(member)
            }
        )

        binding.rvMembers.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = memberAdapter
        }
    }

    private fun setupButtons() {
        binding.btnAddMember.setOnClickListener {
            if (currentGroup != null) {
                showAddMemberDialog()
            } else {
                showSelectGroupFirstDialog()
            }
        }

        binding.btnResetStatus.setOnClickListener {
            if (currentGroup != null) {
                showResetStatusConfirmDialog()
            }
        }

        binding.btnManageGroups.setOnClickListener {
            startActivity(Intent(this, GroupManageActivity::class.java))
        }

        binding.tvGroupName.setOnClickListener {
            showGroupSelectionDialog()
        }
    }

    private fun loadCurrentGroup() {
        val groupId = database.getCurrentGroupId()
        currentGroup = if (groupId != null) {
            database.getGroup(groupId)
        } else {
            val groups = database.getGroups()
            groups.firstOrNull()?.also {
                database.saveCurrentGroupId(it.id)
            }
        }

        updateUI()
    }

    private fun updateUI() {
        if (currentGroup == null) {
            binding.tvGroupName.text = "点击选择小组"
            binding.tvStatistics.text = "请先创建小组"
            binding.tvEmpty.visibility = View.VISIBLE
            binding.rvMembers.visibility = View.GONE
            memberAdapter.updateMembers(emptyList())
        } else {
            binding.tvGroupName.text = currentGroup!!.name
            updateStatistics()

            if (currentGroup!!.members.isEmpty()) {
                binding.tvEmpty.visibility = View.VISIBLE
                binding.rvMembers.visibility = View.GONE
            } else {
                binding.tvEmpty.visibility = View.GONE
                binding.rvMembers.visibility = View.VISIBLE
                memberAdapter.updateMembers(currentGroup!!.members)
            }
        }
    }

    private fun updateStatistics() {
        currentGroup?.let { group ->
            val present = group.getPresentCount()
            val leave = group.getLeaveCount()
            val absent = group.getAbsentCount()
            binding.tvStatistics.text = "已到: $present | 请假: $leave | 未到: $absent"
        }
    }

    // 将这部分代码添加到MainActivity类中（接上一部分）

    private fun toggleMemberStatus(member: Member) {
        member.toggleStatus()
        saveCurrentGroup()
        updateUI()
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
                    addMember(name)
                } else {
                    Toast.makeText(this, "姓名不能为空", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("取消", null)
            .show()
    }

    private fun addMember(name: String) {
        val member = Member(name = name)
        currentGroup?.addMember(member)
        saveCurrentGroup()
        updateUI()
        Toast.makeText(this, "添加成功", Toast.LENGTH_SHORT).show()
    }

    private fun showMemberOptionsDialog(member: Member) {
        val options = arrayOf("编辑姓名", "删除成员")

        AlertDialog.Builder(this)
            .setTitle(member.name)
            .setItems(options) { _, which ->
                when (which) {
                    0 -> showEditMemberDialog(member)
                    1 -> showDeleteMemberConfirmDialog(member)
                }
            }
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
                    saveCurrentGroup()
                    updateUI()
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
                saveCurrentGroup()
                updateUI()
                Toast.makeText(this, "已删除", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("取消", null)
            .show()
    }

    private fun showResetStatusConfirmDialog() {
        AlertDialog.Builder(this)
            .setTitle("重置状态")
            .setMessage("确定要将所有成员状态重置为[未到]吗？")
            .setPositiveButton("重置") { _, _ ->
                currentGroup?.resetAllStatus()
                saveCurrentGroup()
                updateUI()
                Toast.makeText(this, "状态已重置", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("取消", null)
            .show()
    }

    private fun showGroupSelectionDialog() {
        val groups = database.getGroups()

        if (groups.isEmpty()) {
            showSelectGroupFirstDialog()
            return
        }

        val groupNames = groups.map { it.name }.toTypedArray()

        AlertDialog.Builder(this)
            .setTitle("选择小组")
            .setItems(groupNames) { _, which ->
                currentGroup = groups[which]
                database.saveCurrentGroupId(groups[which].id)
                updateUI()
            }
            .show()
    }

    private fun showSelectGroupFirstDialog() {
        AlertDialog.Builder(this)
            .setTitle("提示")
            .setMessage("请先创建小组")
            .setPositiveButton("去创建") { _, _ ->
                startActivity(Intent(this, GroupManageActivity::class.java))
            }
            .setNegativeButton("取消", null)
            .show()
    }

    private fun saveCurrentGroup() {
        currentGroup?.let {
            database.updateGroup(it)
        }
    }
}