# 💰 AccountBook - 智能记账本

[![Android](https://img.shields.io/badge/Android-3DDC84?style=for-the-badge&logo=android&logoColor=white)](https://www.android.com)
[![Kotlin](https://img.shields.io/badge/Kotlin-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white)](https://kotlinlang.org)
[![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-4285F4?style=for-the-badge&logo=jetpack-compose&logoColor=white)](https://developer.android.com/jetpack/compose)
[![License](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)

> 一个现代化的Android记账应用，帮助用户轻松管理个人财务

## 📱 项目简介

AccountBook是一款基于现代Android开发技术栈构建的智能记账应用。应用采用MVVM架构，使用Jetpack Compose构建用户界面，提供直观的记账体验和强大的数据统计功能。

### ✨ 核心特性

- 🔐 **多用户支持** - 安全的用户注册登录系统
- 💰 **智能记账** - 支持收入和支出分类管理
- 📊 **数据统计** - 可视化财务报表和分类分析
- 🔒 **数据隔离** - 确保用户数据隐私和安全
- 📱 **现代化UI** - Material Design 3设计风格
- ⚡ **高性能** - 基于Room数据库的本地存储

## 🚀 主要功能

### 用户管理
- 用户注册与登录
- 安全的密码存储
- 多用户数据隔离
- 用户会话管理

### 记账功能
- 收入/支出分类记录
- 自定义分类管理
- 金额、日期、备注录入
- 记录编辑和删除

### 数据统计
- 总收入/支出统计
- 实时余额计算
- 分类消费分析
- 历史数据可视化

## 🛠️ 技术栈

| 技术组件 | 版本 | 用途 |
|---------|------|------|
| Kotlin | 1.9+ | 开发语言 |
| Jetpack Compose | 1.5+ | UI框架 |
| Room Database | 2.5+ | 本地数据库 |
| ViewModel | 2.6+ | 状态管理 |
| Navigation | 2.6+ | 页面导航 |
| Material Design 3 | 1.1+ | 设计系统 |

## 📥 安装指南

### 环境要求
- Android Studio Arctic Fox 或更高版本
- Android SDK 31 (Android 12) 或更高版本
- Gradle 7.4+ 

### 构建步骤

1. **克隆项目**
```bash
git clone https://github.com/99zwgp/AccountBook.git
cd AccountBook
```

2. **打开项目**
```bash
# 使用Android Studio打开项目
open .
```

3. **同步项目**
```bash
# 同步Gradle依赖
./gradlew build
```

4. **运行应用**
```bash
# 构建并运行应用
./gradlew installDebug
```

### 开发配置

在 `local.properties` 中添加Android SDK路径（如果不存在）：

```properties
sdk.dir=/path/to/your/android/sdk
```

## 📖 使用说明

### 首次使用
1. 启动应用后，点击"注册"创建新账户
2. 输入用户名和密码完成注册
3. 使用注册的凭据登录系统

### 添加记账记录
1. 在主页面点击右下角"+"按钮
2. 选择记录类型（收入/支出）
3. 输入金额和选择分类
4. 添加备注（可选）
5. 点击"保存记录"完成添加

### 查看统计
1. 点击顶部工具栏的统计图标
2. 查看总收入、支出和余额
3. 分析各类别的消费情况

### 编辑记录
1. 在记录列表中长按某条记录
2. 进入编辑页面修改信息
3. 保存更改或取消操作

## 🏗️ 项目结构

```
app/src/main/java/com/example/accountbook/
├── model/              # 数据模型
│   ├── Record.kt       # 记账记录模型
│   ├── User.kt         # 用户模型
│   └── AppDatabase.kt  # 数据库配置
├── repository/         # 数据仓库层
│   ├── RecordRepository.kt
│   └── AuthRepository.kt
├── viewmodel/         # ViewModel层
│   ├── RecordViewModel.kt
│   └── AuthViewModel.kt
├── ui/                # UI组件
│   ├── components/    # 可复用组件
│   ├── navigation/   # 导航配置
│   └── screens/      # 主要页面
└── theme/            # 主题和样式
```

## 🎨 界面预览

### 📊 主要界面

| 功能 | 界面描述 | 截图 |
|------|----------|------|
| 登录页面 | 用户认证入口 | ![登录界面](docs/screenshots/login_screen.png) |
| 记录列表 | 显示所有记账记录 | ![记录列表](docs/screenshots/record_list.png) |
| 添加记录 | 新建记账记录表单 | ![添加记录](docs/imagescreenshotss/add_record.png) |
| 统计页面 | 财务数据可视化 | ![统计页面](docs/screenshots/statistics.png) |

## 📄 许可证

本项目采用 MIT 许可证 - 查看 [LICENSE](LICENSE) 文件了解详情。

## 🔧 开发指南

### 代码规范
- 遵循Kotlin官方编码规范
- 使用Kotlin DSL配置Gradle
- 遵循Material Design设计指南

### 构建测试
```bash
# 运行单元测试
./gradlew test

# 运行UI测试
./gradlew connectedAndroidTest
```

### 代码质量
```bash
# 检查代码风格
./gradlew ktlintCheck

# 格式化代码
./gradlew ktlintFormat
```

## 🤝 贡献指南

我们欢迎任何形式的贡献！请遵循以下步骤：

### 提交Issue
1. 检查是否已存在相关Issue
2. 提供详细的问题描述和复现步骤
3. 如果是功能请求，请说明使用场景

### 提交Pull Request
1. Fork本项目
2. 创建特性分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 开启Pull Request

### 开发规范
- 保持代码风格一致
- 添加必要的单元测试
- 更新相关文档
- 遵循提交信息规范

## 📊 测试计划

项目包含完整的测试计划，详见 [TEST_PLAN.md](TEST_PLAN.md)：

- ✅ 单元测试覆盖核心业务逻辑
- ✅ 集成测试验证模块协作
- ✅ UI测试确保用户体验
- ✅ 性能测试优化应用响应

## 🐛 问题反馈

如果您遇到任何问题，请：

1. 查看[现有Issue](https://github.com/99zwgp/AccountBook/issues)
2. 提供详细的错误信息和复现步骤
3. 包括设备型号和Android版本信息

## 📄 许可证

本项目采用 MIT 许可证 - 查看 [LICENSE](LICENSE) 文件了解详情。

## 🙏 致谢

感谢以下开源项目和技术：

- [Android Jetpack](https://developer.android.com/jetpack) - 现代化Android开发套件
- [Material Design 3](https://m3.material.io/) - 设计系统和组件
- [Kotlin Coroutines](https://kotlinlang.org/docs/coroutines.html) - 异步编程支持

## 📞 联系方式

- **项目主页**: [https://github.com/99zwgp/AccountBook](https://github.com/99zwgp/AccountBook)
- **问题反馈**: [GitHub Issues](https://github.com/99zwgp/AccountBook/issues)
- **开发者**: 99zwgp

---

<div align="center">

**如果这个项目对您有帮助，请给个⭐Star支持一下！**

</div>