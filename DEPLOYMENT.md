# 部署指南（Deployment Guide）

本项目是**前后端分离**架构，两部分需要分开部署：

| 部分 | 托管位置 | 说明 |
|------|----------|------|
| 前端（Vue3） | GitHub Pages | 免费，`https://1eric812.github.io/leave-system/` |
| 后端（Spring Boot + MySQL） | 需要支持 Docker 的云平台 | GitHub Pages 无法运行 Java 和数据库 |

---

## 一、前端：GitHub Pages 自动部署

工作流文件：`.github/workflows/deploy-pages.yml`

### 首次启用（只需做一次）

1. 打开仓库 → **Settings** → 左侧 **Pages**
2. **Build and deployment** → **Source** 选择 **GitHub Actions**
3. 到 **Actions** 标签页，手动跑一次 `Deploy Frontend to GitHub Pages`
   （或往 `main` 分支推一次代码自动触发）

部署完成后访问：`https://1eric812.github.io/leave-system/`

### 关键配置说明

- **`BASE_PATH`**：工作流里注入 `/<仓库名>/`，因为 Pages 部署在子路径下。
  如果没配，静态资源会 404（白屏）。本地开发不受影响。
- **`VITE_API_BASE_URL`**：后端接口地址。在
  **Settings → Secrets and variables → Actions → Variables** 里新建一个
  名为 `VITE_API_BASE_URL` 的变量，值填后端公网地址（含 `/api`），例如：

  ```
  https://your-backend.onrender.com/api
  ```

  不填则前端会请求 `https://1eric812.github.io/api`，接口一定失败。

---

## 二、后端：Docker 部署

已提供两个文件：

- `backend/Dockerfile` —— 多阶段构建（Maven 编译 → 精简 JRE 运行）
- `docker-compose.yml` —— 一键拉起 MySQL 8 + 后端

### 本机 / 服务器上用 Docker 跑

```bash
# 在项目根目录
cp .env.docker.example .env
# 编辑 .env，把 MYSQL_ROOT_PASSWORD 改成你自己的密码
docker compose up -d --build
```

启动后：

- 后端接口：`http://localhost:8080`
- MySQL：`localhost:3306`
- 数据库表和模拟数据由 `schema.sql` / `init_data.sql` 在启动时自动执行

查看日志 / 停止：

```bash
docker compose logs -f backend
docker compose down          # 停止（保留数据卷）
docker compose down -v       # 停止并删除数据库数据
```

### 部署到云平台

以 **Render** 或 **Railway** 为例（都支持 Docker，有免费额度）：

1. 新建项目，关联 GitHub 仓库 `1eric812/leave-system`
2. **Root Directory** 设为 `backend`（这样能找到 Dockerfile）
3. 新建一个 MySQL 实例，拿到连接信息
4. 配置环境变量：

   | 变量 | 说明 |
   |------|------|
   | `DB_URL` | `jdbc:mysql://<host>:<port>/<db>?useUnicode=true&characterEncoding=utf-8&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true&useSSL=false` |
   | `DB_USERNAME` | 数据库用户名 |
   | `DB_PASSWORD` | 数据库密码 |
   | `SERVER_PORT` | 云平台一般会自动注入 `PORT`，容器内保持 `8080` 即可 |

5. 部署完成后，把平台给的公网地址填到前端的 `VITE_API_BASE_URL` 变量里

> **注意**：这是课程学习项目，免费实例通常有休眠机制，首次访问会有冷启动延迟。

---

## 三、环境变量速查

后端通过环境变量读取配置（默认值见 `backend/src/main/resources/application.yml`）：

| 变量 | 默认值 | 说明 |
|------|--------|------|
| `DB_URL` | `jdbc:mysql://localhost:3306/leave_system?...` | 数据库连接串 |
| `DB_USERNAME` | `root` | 数据库用户名 |
| `DB_PASSWORD` | `root` | 数据库密码 |
| `SERVER_PORT` | `8080` | 后端端口 |

前端：

| 变量 | 默认值 | 说明 |
|------|--------|------|
| `VITE_API_BASE_URL` | `/api`（走 Vite 代理） | 后端接口基础地址 |
| `BASE_PATH` | `/` | 构建时的资源基础路径 |

---

## 四、常见问题

**Q：Pages 页面白屏，控制台报 404 加载 js/css**
`BASE_PATH` 没配对。确认工作流里 `BASE_PATH: /${{ github.event.repository.name }}/`，
且仓库名与访问路径一致。

**Q：页面能打开但所有接口都失败**
`VITE_API_BASE_URL` 没设置，或后端没部署/地址填错。
浏览器 F12 → Network 看请求打到了哪个地址。

**Q：后端启动报 `Communications link failure`**
docker compose 场景下，`DB_URL` 里的主机名必须是 `mysql`（服务名），不能写 `localhost`。

**Q：数据库时间差 8 小时**
确认容器时区为 `Asia/Shanghai`（Dockerfile 和 compose 已配置）。
