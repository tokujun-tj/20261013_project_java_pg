import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.sql.Connection; // ★ SQLite保存版
import java.sql.DriverManager; // ★ SQLite保存版
import java.sql.PreparedStatement; // ★ SQLite保存版
import java.sql.ResultSet; // ★ SQLite保存版
import java.sql.SQLException; // ★ SQLite保存版
import java.util.ArrayList; // ★ SQLite保存版
import java.util.List; // ★ SQLite保存版

public class App {
    private static final String DB_URL = "jdbc:sqlite:todos.db"; // ★ CSV保存版から変更

    private static Connection connect() throws SQLException { // ★ CSV保存版から変更
        try { // ★ CSV保存版から変更: SQLite JDBCドライバを明示的に読み込む
            Class.forName("org.sqlite.JDBC"); // ★ CSV保存版から変更: JDBCドライバを登録
        } catch (ClassNotFoundException e) {
            throw new SQLException("sqlite-jdbc JARがクラスパスにありません", e); // ★ CSV保存版から変更
        }
        return DriverManager.getConnection(DB_URL); // ★ CSV保存版から変更
    }

    private static void initializeDatabase() throws SQLException { // ★ CSV保存版から変更
        String sql = "CREATE TABLE IF NOT EXISTS todos (id INTEGER PRIMARY KEY, title TEXT, done INTEGER)"; // ★ CSV保存版から変更
        try (Connection c = connect(); PreparedStatement s = c.prepareStatement(sql)) { // ★ CSV保存版から変更
            s.executeUpdate(); // ★ CSV保存版から変更
        }
    }

    private static void addTodo(String title) { // ★ CSV保存版から変更
        String sql = "INSERT INTO todos (title, done) VALUES (?, ?)"; // ★ CSV保存版から変更
        try (Connection c = connect(); PreparedStatement s = c.prepareStatement(sql)) { // ★ CSV保存版から変更
            s.setString(1, title); // ★ CSV保存版から変更
            s.setInt(2, 0); // ★ CSV保存版から変更
            s.executeUpdate(); // ★ CSV保存版から変更
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private static void completeTodo(int id) { // ★ CSV保存版から変更
        String sql = "UPDATE todos SET done = 1 WHERE id = ?"; // ★ CSV保存版から変更
        try (Connection c = connect(); PreparedStatement s = c.prepareStatement(sql)) { // ★ CSV保存版から変更
            s.setInt(1, id); // ★ CSV保存版から変更
            s.executeUpdate(); // ★ CSV保存版から変更
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private static void deleteTodo(int id) { // ★ CSV保存版から変更
        String sql = "DELETE FROM todos WHERE id = ?"; // ★ CSV保存版から変更
        try (Connection c = connect(); PreparedStatement s = c.prepareStatement(sql)) { // ★ CSV保存版から変更
            s.setInt(1, id); // ★ CSV保存版から変更
            s.executeUpdate(); // ★ CSV保存版から変更
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private static List<Todo> findTodos() { // ★ CSV保存版から変更
        List<Todo> todos = new ArrayList<>(); // ★ CSV保存版から変更
        String sql = "SELECT id, title, done FROM todos ORDER BY id"; // ★ CSV保存版から変更
        try (Connection c = connect(); PreparedStatement s = c.prepareStatement(sql); ResultSet r = s.executeQuery()) { // ★ CSV保存版から変更
            while (r.next()) { // ★ CSV保存版から変更
                todos.add(new Todo(r.getInt("id"), r.getString("title"), r.getInt("done") != 0)); // ★ CSV保存版から変更
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return todos; // ★ CSV保存版から変更
    }

    public static void main(String[] args) throws Exception {
        initializeDatabase(); // ★ CSV保存版から変更
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
        server.createContext("/api/todos", exchange -> { // Todo一覧APIの入口を登録する
            if (!exchange.getRequestMethod().equals("GET")) { // GET以外の要求を拒否する
                exchange.sendResponseHeaders(405, -1); // Method Not Allowedを返す
                exchange.close(); // 応答を閉じる
                return; // API処理を終了する
            } // GET以外の分岐を終える
            byte[] response = todosAsJson().getBytes(StandardCharsets.UTF_8); // Todo一覧をUTF-8のJSONに変換する
            exchange.getResponseHeaders().set("Content-Type", "application/json"); // Content-TypeをJSONだけに設定する
            exchange.sendResponseHeaders(200, response.length); // JSONのHTTP応答を開始する
            exchange.getResponseBody().write(response); // JSON本文を書き込む
            exchange.getResponseBody().close(); // 応答本文を閉じる
        }); // Todo一覧APIの入口を登録する処理を終える
        server.createContext("/", exchange -> {
            String path = exchange.getRequestURI().getPath();
            String method = exchange.getRequestMethod();

            if (path.equals("/add") && method.equals("POST")) {
                String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                if (body.startsWith("todo=")) {
                    String title = URLDecoder.decode(body.substring(5), StandardCharsets.UTF_8);
                    if (!title.isEmpty()) {
                        addTodo(title); // ★ CSV保存版から変更: INSERTを実行
                    }
                }
                redirect(exchange);
                return;
            }

            if (path.equals("/done") && method.equals("GET")) {
                Integer id = queryId(exchange);
                if (id != null) {
                    completeTodo(id); // ★ CSV保存版から変更: UPDATEを実行
                }
                redirect(exchange);
                return;
            }

            if (path.equals("/delete") && method.equals("GET")) {
                Integer id = queryId(exchange);
                if (id != null) {
                    deleteTodo(id); // ★ CSV保存版から変更: DELETEを実行
                }
                redirect(exchange);
                return;
            }

            String message;
            if (path.equals("/")) {
                StringBuilder html = new StringBuilder();
                html.append("<!DOCTYPE html><html><head><meta charset='UTF-8'><title>Todo</title></head><body>");
                html.append("<h1>Todo</h1><form method='post' action='/add'>");
                html.append("<input name='todo'><button>追加</button></form><ul>");
                List<Todo> todos = findTodos(); // ★ CSV保存版から変更: SELECT結果を表示
                for (Todo todo : todos) { // ★ CSV保存版から変更
                    html.append("<li>").append(escapeHtml(todo.title));
                    if (todo.done) {
                        html.append(" &#10003;");
                    }
                    html.append(" <a href='/done?id=").append(todo.id).append("'>完了</a>");
                    html.append(" <a href='/delete?id=").append(todo.id).append("'>削除</a></li>");
                }
                html.append("</ul></body></html>");
                message = html.toString();
                exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
            } else {
                message = "ページが見つかりません";
            }

            byte[] response = message.getBytes(StandardCharsets.UTF_8);
            exchange.sendResponseHeaders(200, response.length);
            exchange.getResponseBody().write(response);
            exchange.getResponseBody().close();
        });
        server.start();
        System.out.println("Server started: http://localhost:8080");
    }

    private static Integer queryId(HttpExchange exchange) { // ★ CSV保存版から変更
        String query = exchange.getRequestURI().getQuery();
        if (query != null && query.startsWith("id=") && query.length() > 3) {
            try {
                return Integer.parseInt(query.substring(3));
            } catch (NumberFormatException ignored) {
                return null;
            }
        }
        return null;
    }

    private static void redirect(HttpExchange exchange) throws java.io.IOException { // ★ CSV保存版から変更
        exchange.getResponseHeaders().set("Location", "/");
        exchange.sendResponseHeaders(303, -1);
        exchange.close();
    }

    private static String escapeHtml(String value) { // ★ CSV保存版から変更
        return value.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;")
                .replace("\"", "&quot;").replace("'", "&#39;");
    }

    private static String todosAsJson() { // Todo一覧をJSON配列に変換する
        StringBuilder json = new StringBuilder("["); // JSON配列の組み立てを開始する
        List<Todo> todos = findTodos(); // データベースから全Todoを取得する
        for (int i = 0; i < todos.size(); i++) { // Todoを順番にJSONへ変換する
            if (i > 0) { // 2件目以降かどうかを確認する
                json.append(','); // JSON要素の区切りを追加する
            } // 区切り判定を終える
            Todo todo = todos.get(i); // 現在のTodoを取り出す
            json.append("{\"title\":\"").append(escapeJson(todo.title)); // タイトルをJSON文字列として追加する
            json.append("\",\"done\":").append(todo.done); // 完了状態をJSON真偽値として追加する
            json.append('}'); // JSONオブジェクトを閉じる
        } // Todoの変換を終える
        return json.append(']').toString(); // JSON配列を閉じて返す
    } // Todo一覧のJSON変換を終える

    private static String escapeJson(String value) { // JSON文字列内の特殊文字をエスケープする
        StringBuilder escaped = new StringBuilder(); // エスケープ後の文字列を組み立てる
        for (int i = 0; i < value.length(); i++) { // タイトルを1文字ずつ処理する
            char ch = value.charAt(i); // 現在の文字を取り出す
            switch (ch) { // JSONで特別扱いが必要な文字を判定する
                case '"' -> escaped.append("\\\""); // ダブルクォートをエスケープする
                case '\\' -> escaped.append("\\\\"); // バックスラッシュをエスケープする
                case '\n' -> escaped.append("\\n"); // 改行をエスケープする
                case '\r' -> escaped.append("\\r"); // 復帰をエスケープする
                case '\t' -> escaped.append("\\t"); // タブをエスケープする
                case '\b' -> escaped.append("\\b"); // バックスペースをエスケープする
                case '\f' -> escaped.append("\\f"); // フォームフィードをエスケープする
                default -> { // その他の文字を処理する
                    if (ch < 0x20) { // JSONで許可されない制御文字か確認する
                        escaped.append(String.format("\\u%04x", (int) ch)); // 制御文字をUnicode表記にする
                    } else { // 通常の文字を処理する
                        escaped.append(ch); // 文字をそのまま追加する
                    } // 制御文字の処理を終える
                } // その他の文字の処理を終える
            } // 文字の判定を終える
        } // タイトルの文字処理を終える
        return escaped.toString(); // エスケープ済み文字列を返す
    } // JSON文字列のエスケープを終える
}

class Todo {
    final int id;
    final String title;
    final boolean done;

    Todo(int id, String title, boolean done) { // ★ CSV保存版から変更
        this.id = id;
        this.title = title;
        this.done = done;
    }
}
