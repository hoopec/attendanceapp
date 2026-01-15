package com.example.attendanceapp.data

import android.content.Context
import android.content.SharedPreferences
import com.example.attendanceapp.model.Group
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class AppDatabase private constructor(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("attendance_db", Context.MODE_PRIVATE)
    private val gson = Gson()

    companion object {
        @Volatile
        private var instance: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return instance ?: synchronized(this) {
                instance ?: AppDatabase(context.applicationContext).also { instance = it }
            }
        }

        private const val KEY_GROUPS = "groups"
        private const val KEY_CURRENT_GROUP_ID = "current_group_id"
    }

    // 保存所有小组
    fun saveGroups(groups: List<Group>) {
        val json = gson.toJson(groups)
        prefs.edit().putString(KEY_GROUPS, json).apply()
    }

    // 获取所有小组
    fun getGroups(): MutableList<Group> {
        val json = prefs.getString(KEY_GROUPS, null) ?: return mutableListOf()
        val type = object : TypeToken<MutableList<Group>>() {}.type
        return gson.fromJson(json, type) ?: mutableListOf()
    }

    // 保存当前选中的小组ID
    fun saveCurrentGroupId(groupId: String) {
        prefs.edit().putString(KEY_CURRENT_GROUP_ID, groupId).apply()
    }

    // 获取当前选中的小组ID
    fun getCurrentGroupId(): String? {
        return prefs.getString(KEY_CURRENT_GROUP_ID, null)
    }

    // 添加小组
    fun addGroup(group: Group) {
        val groups = getGroups()
        groups.add(group)
        saveGroups(groups)
    }

    // 删除小组
    fun deleteGroup(groupId: String) {
        val groups = getGroups()
        groups.removeIf { it.id == groupId }
        saveGroups(groups)

        // 如果删除的是当前小组，清除当前小组ID
        if (getCurrentGroupId() == groupId) {
            prefs.edit().remove(KEY_CURRENT_GROUP_ID).apply()
        }
    }

    // 更新小组
    fun updateGroup(updatedGroup: Group) {
        val groups = getGroups()
        val index = groups.indexOfFirst { it.id == updatedGroup.id }
        if (index != -1) {
            groups[index] = updatedGroup
            saveGroups(groups)
        }
    }

    // 获取指定小组
    fun getGroup(groupId: String): Group? {
        return getGroups().find { it.id == groupId }
    }

    // 清空所有数据
    fun clearAll() {
        prefs.edit().clear().apply()
    }
}