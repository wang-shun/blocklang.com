# `PROJECT_FILE` - 项目文件

当资源类型为`文件`时，将文件的内容存在这里，本表只支持存储文本文件，当前没有存储二进制文件的需求。

## 字段

| 字段名              | 注释         | 类型 | 长度 | 默认值 | 主键 | 可空 |
| ------------------- | ------------ | ---- | ---- | ------ | ---- | ---- |
| dbid                | 主键         | int  |      |        | 是   | 否   |
| project_resource_id | 项目资源标识 | int  |      |        |      | 否   |
| file_type           | 文件类型     | char | 2    |        |      | 否   |
| content             | 内容         | text |      |        |      | 是   |

## 约束

* 主键：`PK_PROJECT_FILE`
* 外键：(*未设置*)`FK_PROJECT_FILE_RESOURCE`，`PROJECT_RESOURCE_ID` 对应 `PROJECT_RESOURCE` 表的 `dbid`
* 索引：`UK_PROJECT_RESOURCE_ID`，对应字段 `project_resource_id`

## 说明

1. `file_type` 的值为：`01` 表示 `markdown`
2. 注意，本表中不包含 4 个辅助字段