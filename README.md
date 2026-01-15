# 考勤点名 App

一款简洁美观的 Android 考勤点名应用，帮助老师或小组长快速完成成员签到统计。

## 功能特性

### ✨ 核心功能

- **多小组管理**：支持创建、删除、重命名多个小组，灵活切换
- **快速点名**：点击成员姓名即可切换考勤状态（未到 → 已到 → 请假）
- **实时统计**：自动统计并显示已到、请假、未到人数
- **成员管理**：支持添加、编辑、删除成员信息
- **数据持久化**：所有数据本地保存，关闭应用后数据不丢失
- **状态重置**：一键重置所有成员的考勤状态

### 🎨 界面设计

- Material Design 3 设计风格
- 卡片式布局，简洁美观
- 全面屏适配
- 状态颜色区分（红色-未到、绿色-已到、橙色-请假）

## 技术栈

- **开发语言**：Kotlin
- **最低 SDK**：API 24 (Android 7.0)
- **目标 SDK**：API 35 (Android 16)
- **架构组件**：
    - ViewBinding
    - RecyclerView
    - Material Design Components
    - SharedPreferences + Gson（数据持久化）

## 项目创建步骤

### 1. 环境准备

- 安装 [Android Studio](https://developer.android.com/studio)（推荐最新稳定版）
- 确保已安装 Android SDK API 35

### 2. 创建新项目

1. 打开 Android Studio
2. 选择 `File` → `New` → `New Project`
3. 选择 `Empty Activity`
4. 配置项目：
    - **Name**: `AttendanceApp`
    - **Package name**: `com.example.attendanceapp`
    - **Save location**: 选择你的项目位置
    - **Language**: **Kotlin**
    - **Minimum SDK**: **API 24 ("Nougat"; Android 7.0)**
    - **Build configuration language**: **Kotlin DSL**
5. 点击 `Finish` 等待项目创建完成

### 3. 配置 Gradle

更新 `app/build.gradle.kts` 配置项

### 4. 同步项目

- 点击 Android Studio 顶部的 **Sync Now** 按钮
- 或选择 `File` → `Sync Project with Gradle Files`

## 项目结构

```
app/src/main/
├── java/com/example/attendanceapp/
│   ├── model/                    # 数据模型
│   │   ├── AttendanceStatus.kt   # 考勤状态枚举
│   │   ├── Member.kt             # 成员数据类
│   │   └── Group.kt              # 小组数据类
│   ├── data/                     # 数据层
│   │   └── AppDatabase.kt        # 数据持久化管理
│   ├── adapter/                  # 适配器
│   │   ├── MemberAdapter.kt      # 成员列表适配器
│   │   └── GroupAdapter.kt       # 小组列表适配器
│   └── ui/                       # UI层
│       ├── MainActivity.kt       # 主界面（点名）
│       ├── GroupManageActivity.kt     # 小组管理
│       └── MemberManageActivity.kt    # 成员管理
├── res/
│   ├── layout/                   # 布局文件
│   ├── values/                   # 资源文件
│   ├── drawable/                 # 图标资源
│   ├── menu/                     # 菜单文件
│   └── xml/                      # XML配置
└── AndroidManifest.xml           # 应用清单
```

## 运行步骤

### 方式一：使用真机

1. 在 Android 手机上开启**开发者选项**和 **USB 调试**
2. 用 USB 线连接手机到电脑
3. 在 Android Studio 顶部工具栏选择你的设备
4. 点击绿色的 **Run** 按钮 ▶️
5. 等待应用安装并自动启动

### 方式二：使用模拟器

1. 点击 Android Studio 顶部工具栏的 **Device Manager**
2. 点击 **Create Device**
3. 选择设备型号（推荐 Pixel 系列）
4. 选择系统镜像（推荐 API 35）
5. 点击 **Finish** 创建模拟器
6. 启动模拟器后点击 **Run** 按钮 ▶️

## 使用指南

### 首次使用

1. **创建小组**
    - 打开应用后点击「管理小组」
    - 点击右下角的 ➕ 按钮
    - 输入小组名称（如"第一小组"）
    - 点击「创建」

2. **添加成员**
    - 返回主界面
    - 点击「添加成员」按钮
    - 输入成员姓名
    - 点击「添加」

3. **开始点名**
    - 看到成员后，点击该成员的姓名
    - 状态会循环切换：未到(红) → 已到(绿) → 请假(橙)
    - 顶部会实时显示统计数据

### 常用操作

#### 管理成员
- **编辑成员**：长按成员 → 选择「编辑姓名」
- **删除成员**：长按成员 → 选择「删除成员」
- **重置状态**：点击「重置状态」按钮，所有成员状态重置为「未到」

#### 管理小组
- **切换小组**：点击顶部的小组名称，选择要切换的小组
- **重命名小组**：在小组管理界面长按小组 → 选择「重命名」
- **删除小组**：在小组管理界面长按小组 → 选择「删除小组」
- **管理成员**：在小组管理界面长按小组 → 选择「管理成员」

## 数据存储

应用使用 **SharedPreferences** 结合 **Gson** 进行数据持久化：

- 存储位置：`/data/data/com.example.attendanceapp/shared_prefs/attendance_db.xml`
- 存储内容：所有小组信息、成员信息、考勤状态
- 数据格式：JSON
- 自动保存：每次操作后立即保存

## 常见问题

### Q: 应用闪退怎么办？
**A:**
1. 检查 Logcat 中的错误信息
2. 确保已正确同步 Gradle
3. 清理项目：`Build` → `Clean Project`
4. 重新构建：`Build` → `Rebuild Project`

### Q: 数据丢失了怎么办？
**A:** 数据存储在应用内部，只有在以下情况会丢失：
- 卸载应用
- 清除应用数据
- 系统恢复出厂设置

### Q: 如何备份数据？
**A:** 当前版本暂不支持导出功能，后续版本会考虑添加。

### Q: 支持多少个小组和成员？
**A:** 理论上无限制，但建议：
- 小组数量：< 50 个
- 每个小组成员：< 100 人

## 开发计划

- [ ] 数据导出功能（CSV/Excel）
- [ ] 历史记录查询
- [ ] 考勤报表统计
- [ ] 云端数据同步
- [ ] 深色模式支持
- [ ] 多语言支持

## 开源协议

MIT License

---
