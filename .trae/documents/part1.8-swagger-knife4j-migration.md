# Part 1.8 Swagger/Knife4j 迁移方案

## Context

当前项目编译阻塞在 Swagger/Knife4j 迁移：`io.swagger.annotations.*` 与 `springfox.documentation.*` 已不存在，但 52 个文件仍在引用。需将 Swagger 2 / Springfox 配置迁移到 Knife4j 4.x（基于 SpringDoc OpenAPI 3），打通 `mvn compile`。

## Scope

* **配置重写**：`Knife4jConfig.java`（Springfox `Docket` → SpringDoc `OpenAPI` + `GroupedOpenApi`）

* **注解迁移**：49 个文件、约 321 处 Swagger 2 注解替换为 OpenAPI 3 注解

* **反射逻辑改造**：`OperationLogAspect.java`、`ExceptionLogAspect.java` 读取注解的逻辑同步更新

* **依赖**：`pom.xml` 中 `knife4j-openapi3-jakarta-spring-boot-starter:4.5.0` 已正确，无需改动

## Implementation Approach

### 1. 重写 Knife4jConfig.java

保留原有文档标题、描述、联系人、版本、服务地址、扫描包限制：

* 使用 `io.swagger.v3.oas.models.OpenAPI` + `Info` + `Contact` + `Server`

* 使用 `org.springdoc.core.models.GroupedOpenApi` 限定扫描 `com.aurora.controller`

* 删除 `@EnableSwagger2WebMvc` 和 Springfox 相关类

### 2. 批量注解替换

| Swagger 2                                           | OpenAPI 3                    | import 路径                                    |
| --------------------------------------------------- | ---------------------------- | -------------------------------------------- |
| `@Api(tags = "X")`                                  | `@Tag(name = "X")`           | `io.swagger.v3.oas.annotations.tags.Tag`     |
| `@ApiOperation("X")` / `@ApiOperation(value = "X")` | `@Operation(summary = "X")`  | `io.swagger.v3.oas.annotations.Operation`    |
| `@ApiModel(description = "X")`                      | `@Schema(description = "X")` | `io.swagger.v3.oas.annotations.media.Schema` |
| `@ApiModelProperty(...)`                            | `@Schema(...)`               | `io.swagger.v3.oas.annotations.media.Schema` |
| `@ApiImplicitParam(...)`                            | 迁移为参数级 `@Parameter(...)`     | `io.swagger.v3.oas.annotations.Parameter`    |

`@ApiModelProperty` 映射细节：

* `value` → `description`

* `name`：若与字段名一致可省略；若不一致（如 `WebsiteConfigVO` 中 `englishName` 字段的 `name = "nickName"`）保留为 `name`

* `required = true` → `requiredMode = Schema.RequiredMode.REQUIRED`

* `dataType` → 优先省略，由 SpringDoc 根据 Java 类型自动推断；特殊场景（如 `MultipartFile`）显式指定 `type = "string", format = "binary"`

**注意** **`name`/`value`** **反写文件**：`AboutVO`、`JobRunVO`、`JobStatusVO`、`JobSearchVO`、`JobLogSearchVO` 中 `name` 是中文描述、`value` 是字段名，需人工修正为 `description = 中文描述`，不可直接映射。

### 3. `@ApiImplicitParam` 处理方法

共 13 处，全部改为参数级 `@Parameter`：

```java
// 改造前
@ApiOperation("根据id查看后台文章")
@ApiImplicitParam(name = "articleId", value = "文章id", required = true, dataType = "Integer")
@GetMapping("/admin/articles/{articleId}")
public ResultVO<...> getArticleBackById(@PathVariable("articleId") Integer articleId) { ... }

// 改造后
@Operation(summary = "根据id查看后台文章")
@GetMapping("/admin/articles/{articleId}")
public ResultVO<...> getArticleBackById(
        @Parameter(description = "文章id", required = true)
        @PathVariable("articleId") Integer articleId) { ... }
```

### 4. Aspect 反射逻辑改造

`OperationLogAspect.java`：

* `Api api = ...getAnnotation(Api.class)` → `Tag tag = ...getAnnotation(Tag.class)`

* `api.tags()[0]` → `tag.name()`（需判空）

* `ApiOperation apiOperation = ...getAnnotation(ApiOperation.class)` → `Operation operation = ...getAnnotation(Operation.class)`

* `apiOperation.value()` → `operation.summary()`（需判空）

`ExceptionLogAspect.java`：

* `ApiOperation apiOperation = ...getAnnotation(ApiOperation.class)` → `Operation operation = ...getAnnotation(Operation.class)`

* `apiOperation.value()` → `operation.summary()`（需判空）

### 5. 清理与验证

* 清理所有 `io.swagger.annotations.*` 和 `springfox.*` import

* 运行 `mvn clean compile` 确认编译通过

* 全局搜索确认无残留 `@ApiOperation`、`@ApiImplicitParam`、`@Api`、`@ApiModel`、`@ApiModelProperty`、`springfox`、`io.swagger.annotations`

## Critical Files

* [Knife4jConfig.java](file:///d:/0trea-make-app/小学期作业/重构版/aurora-springboot/src/main/java/com/aurora/config/Knife4jConfig.java)

* [OperationLogAspect.java](file:///d:/0trea-make-app/小学期作业/重构版/aurora-springboot/src/main/java/com/aurora/aspect/OperationLogAspect.java)

* [ExceptionLogAspect.java](file:///d:/0trea-make-app/小学期作业/重构版/aurora-springboot/src/main/java/com/aurora/aspect/ExceptionLogAspect.java)

* Controller 包：19 个文件（如 `ArticleController.java`、`UserAuthController.java` 等）

* VO/DTO 包：约 30 个文件（如 `ConditionVO.java`、`WebsiteConfigVO.java`、`AboutVO.java`、`JobRunVO.java` 等）

## Verification

1. **编译验证**

   ```powershell
   $env:JAVA_HOME="D:\Develop\JDK25\jdk-25.0.3+9"
   d:\Develop\Maven\apache-maven-3.9.16\bin\mvn.cmd clean compile -f "d:\0trea-make-app\小学期作业\重构版\aurora-springboot\pom.xml" -q
   ```

2. **运行时验证**（编译通过后可选）

   * 启动应用，访问 `http://localhost:8080/doc.html`

   * 确认 Controller 按 `@Tag(name = ...)` 分组、接口 `summary` 正确、参数描述正确

## Notes

* 迁移过程中保持 `aurora/` 原项目只读不变

* 本次改造仅做注解替换与配置重写，不引入新的业务功能

* 若某些 VO 字段类型与旧 `dataType` 不一致，以实际 Java 类型为准，由 SpringDoc 自动推断

