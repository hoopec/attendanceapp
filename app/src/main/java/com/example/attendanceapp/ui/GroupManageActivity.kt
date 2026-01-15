package com.example.attendanceapp.ui

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.attendanceapp.adapter.GroupAdapter
import com.example.attendanceapp.data.AppDatabase
import com.example.attendanceapp.databinding.ActivityGroupManageBinding
import com.example.attendanceapp.model.Group

class GroupManageActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGroupManageBinding
    private lateinit var database: AppDatabase
    private lateinit var groupAdapter: GroupAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGroupManageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        database = AppDatabase.getInstance(this)

        setupToolbar()
        setupRecyclerView()
        setupFab()
        loadGroups()
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    private fun setupRecyclerView() {
        groupAdapter = GroupAdapter(
            mutableListOf(),
            onGroupClick = { group ->
                openMemberManage(group)
            },
            onGroupLongClick = { group ->
                showGroupOptionsDialog(group)
            }
        )

        binding.rvGroups.apply {
            layoutManager = LinearLayoutManager(this@GroupManageActivity)
            adapter = groupAdapter
        }
    }

    private fun setupFab() {
        binding.fabAddGroup.setOnClickListener {
            showAddGroupDialog()
        }
    }

    private fun loadGroups() {
        val groups = database.getGroups()

        if (groups.isEmpty()) {
            binding.tvEmpty.visibility = View.VISIBLE
            binding.rvGroups.visibility = View.GONE
        } else {
            binding.tvEmpty.visibility = View.GONE
            binding.rvGroups.visibility = View.VISIBLE
            groupAdapter.updateGroups(groups)
        }
    }

    private fun showAddGroupDialog() {
        val input = EditText(this)
        input.hint = "输入小组名称"
        input.setPadding(50, 30, 50, 30)

        AlertDialog.Builder(this)
            .setTitle("新建小组")
            .setView(input)
            .setPositiveButton("创建") { _, _ ->
                val name = input.text.toString().trim()
                if (name.isNotEmpty()) {
                    createGroup(name)
                } else {
                    Toast.makeText(this, "名称不能为空", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("取消", null)
            .show()
    }

    private fun createGroup(name: String) {
        val group = Group(name = name)
        database.addGroup(group)

        // 如果是第一个小组，自动设为当前小组
        if (database.getGroups().size == 1) {
            database.saveCurrentGroupId(group.id)
        }

        loadGroups()
        Toast.makeText(this, "创建成功", Toast.LENGTH_SHORT).show()
    }

    private fun showGroupOptionsDialog(group: Group) {
        val options = arrayOf("管理成员", "重命名", "删除小组")

        AlertDialog.Builder(this)
            .setTitle(group.name)
            .setItems(options) { _, which ->
                when (which) {
                    0 -> openMemberManage(group)
                    1 -> showRenameGroupDialog(group)
                    2 -> showDeleteGroupConfirmDialog(group)
                }
            }
            .show()
    }

    private fun openMemberManage(group: Group) {
        val intent = Intent(this, MemberManageActivity::class.java)
        intent.putExtra("group_id", group.id)
        startActivity(intent)
    }

    private fun showRenameGroupDialog(group: Group) {
        val input = EditText(this)
        input.setText(group.name)
        input.hint = "输入新名称"
        input.setPadding(50, 30, 50, 30)
        input.selectAll()

        AlertDialog.Builder(this)
            .setTitle("重命名小组")
            .setView(input)
            .setPositiveButton("保存") { _, _ ->
                val newName = input.text.toString().trim()
                if (newName.isNotEmpty()) {
                    group.name = newName
                    database.updateGroup(group)
                    loadGroups()
                    Toast.makeText(this, "修改成功", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "名称不能为空", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("取消", null)
            .show()
    }

    private fun showDeleteGroupConfirmDialog(group: Group) {
        AlertDialog.Builder(this)
            .setTitle("删除小组")
            .setMessage("确定要删除 ${group.name} 吗？这将删除该小组的所有成员数据。")
            .setPositiveButton("删除") { _, _ ->
                database.deleteGroup(group.id)
                loadGroups()
                Toast.makeText(this, "已删除", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("取消", null)
            .show()
    }

    override fun onResume() {
        super.onResume()
        loadGroups()
    }
}