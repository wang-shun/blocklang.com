# `PROJECT` - 项目基本信息

## 字段

| 字段名           | 注释         | 类型     | 长度 | 默认值 | 主键 | 可空 |
| ---------------- | ------------ | -------- | ---- | ------ | ---- | ---- |
| dbid             | 主键         | int      |      |        | 是   | 否   |
| project_name     | 项目名称     | varchar  | 32   |        |      | 否   |
| project_desc     | 项目描述     | varchar  | 128  |        |      | 是   |
| is_private       | 是否私有     | boolean  |      | false  |      | 否   |
| last_active_time | 最近活动时间 | datetime |      |        |      | 否   |
| avatar_url       | 项目 Logo    | varchar  | 256  |        |      | 是   |

## 约束

* 主键：`PK_PROJECT`
* 外键：无
* 索引：`UK_PROJECT_NAME_CREATE_USER_ID`，对应字段 `project_name`、`create_user_id`

## 说明

1. `last_active_time` 是一个冗余字段，记录项目内容的最近修改时间，而 `last_update_time` 记录的是此表中项目基本信息的最近修改时间
2. `homepage` 用于配置项目运行在测试环境下的主页链接，暂时不存储，因为可根据规则生成链接
