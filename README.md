# TodayClass Android

TodayClass 是一个本地课表管理 Android 应用，支持从 Excel 课表导入课程、查看今日课表和周课表、编辑课程排课信息，并通过 GitHub Actions 构建发布 release APK。

这份 README 面向二次开发者，重点说明项目结构、开发环境、构建发布流程和主要扩展点。

## 技术栈

- Kotlin
- Jetpack Compose + Material 3
- AndroidX Navigation3
- Koin 依赖注入
- Room 本地数据库
- DataStore 偏好设置
- Apache POI 解析 Excel
- FileKit 文件选择
- Gradle Version Catalog

## 环境要求

- Android Studio / IntelliJ IDEA
- JDK 17
- Android Gradle Plugin 9.2.1
- Kotlin 2.3.21
- Android SDK Platform 37.0
- minSdk 30
- targetSdk 36
- compileSdk 37

首次打开项目后，建议先同步 Gradle，再运行：

```bash
./gradlew testDebugUnitTest
./gradlew assembleDebug
```

## 项目结构

```text
app/src/main/java/com/eggetteluo/todayclass
├── data
│   ├── datastore      # 主题等偏好设置
│   ├── local          # Room 数据库、DAO、Entity、Relation
│   └── model          # UI/业务数据模型
├── di                 # Koin 模块、导航 entry 注入
├── feature
│   ├── home           # 今日课表
│   ├── schedule       # 课程排课详情/编辑
│   ├── setting        # 设置页
│   ├── upload         # Excel 课表导入
│   └── week           # 周课表
├── navigation         # Route、BottomTab、Navigator
├── ui
│   ├── components     # 通用 Compose 组件
│   ├── root           # 根 Scaffold/NavDisplay/CompositionLocal
│   └── theme          # 主题、色彩、字体
└── util               # Excel、课程解析、日期、颜色等工具
```

## 架构约定

项目按 feature 分层组织。页面入口由 `NavigationModule` 统一提供 ViewModel 和导航回调，Screen 本身只负责 UI 状态展示和事件分发。

推荐新增页面时遵循现有模式：

1. 在 `navigation/AppRoute.kt` 添加 Route。
2. 在 `feature/<name>` 下添加 `Screen`、`ViewModel`、`UiState`。
3. 在 `di/ViewModelModule.kt` 注册 ViewModel。
4. 在 `di/NavigationModule.kt` 注册 navigation entry，并把 ViewModel / 导航动作传给 Screen。
5. 避免在 Screen 内直接调用 `koinViewModel()`、`koinInject()` 或直接依赖 `Navigator`。

## 数据库

Room 入口：

```text
data/local/AppDatabase.kt
```

主要表：

- `CourseEntity`：课程基础信息
- `CourseScheduleEntity`：排课主表
- `CourseScheduleWeekEntity`：排课对应周次
- `CourseTimeRuleEntity`：节次时间规则
- `SemesterInfoEntity`：学期信息

首次创建数据库时会通过 `DefaultTimeRules` 写入默认上课时间规则。

如果修改 Entity 字段，需要同步处理：

- `AppDatabase` version
- migration
- DAO 查询字段
- 导入和编辑逻辑

当前 `exportSchema = false`，如果后续要维护正式数据库迁移，建议开启 schema 导出并提交 schema 文件。

## Excel 导入流程

入口：

```text
feature/upload/UploadScreen.kt
feature/upload/UploadViewModel.kt
util/ExcelUtil.kt
util/CourseUtil.kt
```

流程：

1. FileKit 选择 `.xlsx` 文件。
2. `ExcelUtil` 读取首个 sheet，并处理合并单元格。
3. `CourseUtil.parseCellText` 解析单元格课程文本。
4. 用户确认当前周后，`UploadViewModel.saveToDatabase` 写入 Room。

如果学校课表格式变化，优先调整 `CourseUtil` 和 `ExcelUtil`，避免把解析细节扩散到 UI 层。

## 常用命令

运行单元测试：

```bash
./gradlew testDebugUnitTest
```

构建 debug APK：

```bash
./gradlew assembleDebug
```

构建 release APK：

```bash
./gradlew assembleRelease
```

生成的 APK 文件名格式：

```text
TodayClass-$versionName-v$versionCode-debug.apk
TodayClass-$versionName-v$versionCode-release.apk
```

例如：

```text
app/build/outputs/apk/debug/TodayClass-1.1.0-v6-debug.apk
app/build/outputs/apk/release/TodayClass-1.1.0-v6-release.apk
```

## Release 签名

Release 签名通过环境变量注入，避免把密码写入仓库。

本地如需构建已签名 release APK，可设置：

```bash
export SIGNING_KEY_STORE_PATH=/absolute/path/to/release-key.jks
export SIGNING_KEY_STORE_PASSWORD=your_store_password
export SIGNING_KEY_ALIAS=your_key_alias
export SIGNING_KEY_PASSWORD=your_key_password

./gradlew assembleRelease
```

如果未设置这些环境变量，本地仍可执行 `assembleRelease`，但产物不会使用 release keystore 签名。

## GitHub Actions 发布

发布工作流：

```text
.github/workflows/release.yml
```

触发方式：

- 手动运行 `Release APK`
- 推送匹配 `v*` 或 `*-v*` 的 tag

发布 tag 示例：

```bash
git tag 1.1.0-v6
git push origin 1.1.0-v6
```

GitHub 仓库需要配置以下 Actions Secrets：

```text
RELEASE_KEYSTORE_BASE64
RELEASE_KEYSTORE_PASSWORD
RELEASE_KEY_ALIAS
RELEASE_KEY_PASSWORD
```

生成 `RELEASE_KEYSTORE_BASE64`：

```bash
base64 -i app/TC_key.jks | pbcopy
```

查看 keystore alias：

```bash
keytool -list -v -keystore app/TC_key.jks
```

工作流会执行：

1. 安装 JDK 17。
2. 配置 Gradle。
3. 配置 Android SDK。
4. 安装 `platforms;android-37.0`。
5. 解码 release keystore。
6. 执行 `testDebugUnitTest assembleRelease`。
7. 校验 APK 文件存在。
8. 使用 `apksigner verify` 校验签名。
9. 创建 GitHub Release 并上传 APK。

## 版本发布约定

版本号在 `app/build.gradle.kts` 中维护：

```kotlin
versionCode = 6
versionName = "1.1.0"
```

发布前建议：

1. 更新 `versionCode`。
2. 必要时更新 `versionName`。
3. 本地运行测试和 release 构建。
4. 提交变更。
5. 创建并推送 tag。

## 开发注意事项

- 不要把 keystore 密码、GitHub token、签名环境变量写入仓库。
- `local.properties` 是本地 SDK 配置，不应依赖它做 CI 配置。
- 新增依赖优先写入 `gradle/libs.versions.toml`。
- 业务逻辑优先放在 ViewModel、DAO、util 中，Screen 保持可组合、可预览、低耦合。
- 修改数据库结构时不要只改 Entity，需要补 migration 或明确处理数据兼容。
- 解析 Excel 时尽量保留原始文本，方便排查不同学校课表格式差异。

## 贡献建议

提交前建议至少运行：

```bash
./gradlew testDebugUnitTest
```

涉及 UI、导航、数据库、导入流程或发布配置的改动，建议额外运行：

```bash
./gradlew assembleDebug
./gradlew assembleRelease
```
