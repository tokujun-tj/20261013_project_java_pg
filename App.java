import com.sun.net.httpserver.HttpServer; // Java標準の小さなWebサーバーを使います
import java.net.InetSocketAddress; // 待ち受ける住所とポート番号を指定します
import java.net.URLDecoder; // フォームで送られた日本語を元に戻します
import java.nio.charset.StandardCharsets; // 文字コードを指定します
import java.util.ArrayList; // Todoを入れるリストを用意します
import java.util.List; // 文字列のリストを使います

public class App { // Appという名前のプログラムを定義します
    static List<Todo> todos = new ArrayList<>(); // ★変更 Todoを保存するリストです
    static int nextId = 1; // ★変更 次に使うTodoの番号です

    public static void main(String[] args) throws Exception { // プログラムの実行開始地点です
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0); // 8080番ポートでサーバーを用意します
        todos.add(new Todo(nextId++, "牛乳を買う")); // ★変更 起動時のサンプルTodoを追加します
        Todo egg = new Todo(nextId++, "卵を買う"); // ★変更 起動時のサンプルTodoを作ります
        egg.setDone(true); // ★変更 卵を買うを完了にします
        todos.add(egg); // ★変更 サンプルTodoを追加します
        server.createContext("/", exchange -> { // "/"へのアクセスを受け取る処理を登録します
            String path = exchange.getRequestURI().getPath(); // アクセスされたパスを取り出します
            String method = exchange.getRequestMethod(); // GETやPOSTなどの方法を取り出します
            String message;
            exchange.getResponseHeaders().set("Content-Type", "text/plain; charset=UTF-8"); // 通常の応答はUTF-8のテキストとして返します
            if (path.equals("/add") && method.equals("POST")) { // フォームから送られたTodoを追加します
                String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8); // 送信された内容をUTF-8で読み取ります
                String value = body.substring(5); // todo=の後ろを取り出します
                String title = URLDecoder.decode(value, StandardCharsets.UTF_8); // ★変更 日本語を元の文字に戻してtitleに入れます
                if (!title.isEmpty()) { // ★変更 空の入力でなければリストに追加します
                    todos.add(new Todo(nextId, title)); // ★変更 Todoを作ってリストに追加します
                    nextId++; // ★変更 次の番号に進めます
                } // 空の入力を追加しない処理はここまでです
                exchange.getResponseHeaders().set("Location", "/"); // 戻り先をトップページにします
                exchange.sendResponseHeaders(303, -1); // トップページへ移動する応答を返します
                exchange.close(); // 応答を閉じます
                return; // この分岐の処理を終えます
            } else if (path.equals("/done") && method.equals("GET")) { // ★追加 完了リンクの処理です
                String query = exchange.getRequestURI().getQuery(); // ★追加 URLのidを受け取ります
                if (query != null && query.startsWith("id=") && query.length() > 3) { // ★追加 idがあるか確認します
                    try { // ★追加 idを数字に変える処理です
                        int id = Integer.parseInt(query.substring(3)); // ★追加 idを数に変えます
                        for (Todo todo : todos) { // ★追加 Todoを1件ずつ確認します
                            if (todo.getId() == id) { // ★追加 idが一致するか確認します
                                todo.setDone(true); // ★追加 Todoを完了にします
                                break; // ★追加 一致したTodoの確認を終えます
                            } // ★追加 idが一致した場合の処理はここまでです
                        } // ★追加 Todoの確認はここまでです
                    } catch (NumberFormatException e) { // ★追加 数字でないidは何もしません
                    } // ★追加 idを数字に変える処理はここまでです
                } // ★追加 idの確認と完了処理はここまでです
                exchange.getResponseHeaders().set("Location", "/"); // ★追加 一覧に戻ります
                exchange.sendResponseHeaders(303, -1); // ★追加 一覧へ移動する応答を返します
                exchange.close(); // ★追加 応答を閉じます
                return; // ★追加 この分岐の処理を終えます
            } else if (path.equals("/delete") && method.equals("GET")) { // ★追加 削除リンクの処理です
                String query = exchange.getRequestURI().getQuery(); // ★追加 URLのidを受け取ります
                if (query != null && query.startsWith("id=") && query.length() > 3) { // ★追加 idがあるか確認します
                    try { // ★追加 idを数字に変える処理です
                        int id = Integer.parseInt(query.substring(3)); // ★追加 idを数に変えます
                        todos.remove(0); // ★追加 idが一致するTodoを削除します
                    } catch (NumberFormatException e) { // ★追加 数字でないidは何もしません
                    } // ★追加 idを数字に変える処理はここまでです
                } // ★追加 idの確認と削除処理はここまでです
                exchange.getResponseHeaders().set("Location", "/"); // ★追加 一覧に戻ります
                exchange.sendResponseHeaders(303, -1); // ★追加 一覧へ移動する応答を返します
                exchange.close(); // ★追加 応答を閉じます
                return; // ★追加 この分岐の処理を終えます
            } else if (path.equals("/")) { // トップページにフォームとTodo一覧を表示します
                String html = "<!DOCTYPE html><html><head><meta charset='UTF-8'><title>わたしのTodo</title><style>body { max-width: 640px; margin: 24px auto; padding: 0 16px; font-size: 16px; } h1 { font-size: 24px; } input, button { font-size: 16px; }</style></head><body><h1>わたしのTodo</h1><form method='post' action='/add'><input name='todo'><button>追加</button></form><ul>"; // Todo追加フォームと一覧を始めます
                for (Todo todo : todos) { // ★変更 リストのTodoを1件ずつ取り出します
                    String mark = ""; // ★変更 完了印を空にしておきます
                    if (todo.isDone()) { // ★変更 完了したTodoか確認します
                        mark = "✓"; // ★変更 完了印を付けます
                    } // ★変更 完了印の処理はここまでです
                    html += "<li>" + todo.getTitle() + mark + " <a href='/done?id=" + todo.getId()
                            + "'>完了</a> <a href='/delete?id=" + todo.getId() + "'>削除</a></li>"; // ★追加
                                                                                                // Todoのタイトルと完了・削除リンクをHTMLに追加します
                } // Todoをすべて追加し終えます
                if (todos.isEmpty()) {
                    html += "<p>やることは、いまゼロです</p>";
                }
                html += "</ul></body></html>"; // HTMLのリストを閉じます
                message = html; // 組み立てたHTMLを応答にします
                exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8"); // トップページをHTMLとして返します
            } else {
                message = "ページが見つかりません"; // ★変更 どのページでもない場合の応答です
            }
            byte[] body = message.getBytes("UTF-8"); // 返す文字をUTF-8のデータに変換します
            exchange.sendResponseHeaders(200, body.length); // 正常な応答とデータの長さを送ります
            exchange.getResponseBody().write(body); // 変換したデータをブラウザに送ります
            exchange.getResponseBody().close(); // 送信を終えて応答を閉じます
        }); // "/"への処理の登録はここまでです
        server.start(); // サーバーの待ち受けを開始します
        System.out.println("サーバー起動: http://localhost:8080 （止めるときは Ctrl+C）"); // 起動したことをターミナルに表示します
    } // mainメソッドはここまでです
} // Appクラスはここまでです

class Todo { // ★変更 Todoを表すクラスです
    private final int id; // ★変更 Todoの番号です
    private final String title; // ★変更 やることです
    private boolean done; // ★変更 終わったかどうかです

    Todo(int id, String title) { // ★変更 Todoを作るときの処理です
        this.id = id; // ★変更 番号を保存します
        this.title = title; // ★変更 やることを保存します
        this.done = false; // ★変更 最初は未完了にします
    }

    int getId() { // ★変更 番号を読み出します
        return id; // ★変更 番号を返します
    }

    String getTitle() { // ★変更 やることを読み出します
        return title; // ★変更 やることを返します
    }

    boolean isDone() { // ★変更 完了したかを読み出します
        return done; // ★変更 完了状態を返します
    }

    void setDone(boolean done) { // ★変更 完了状態を書き換えます
        this.done = done; // ★変更 完了状態を保存します
    }
}
