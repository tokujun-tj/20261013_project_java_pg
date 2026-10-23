import com.sun.net.httpserver.HttpServer; // Java標準の小さなWebサーバーを使います
import java.net.InetSocketAddress; // 待ち受ける住所とポート番号を指定します
public class App { // Appという名前のプログラムを定義します
    public static void main(String[] args) throws Exception { // プログラムの実行開始地点です
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0); // 8080番ポートでサーバーを用意します
        server.createContext("/", exchange -> { // "/"へのアクセスを受け取る処理を登録します
            String path = exchange.getRequestURI().getPath(); // アクセスされたパスを取り出します
            String message;
            if (path.equals("/hello")) { // パスが「/hello」か比べます
                message = "こんにちは！";
            } else if (path.equals("/bye")) {
                message = "さようなら！";
            } else if (path.equals("/menu")) { // パスが「/menu」か比べます
                message = "今日のお昼はカレー";
            } else {
                message = "ページが見つかりません";
            }
            exchange.getResponseHeaders().set("Content-Type", "text/plain; charset=UTF-8"); // UTF-8の文字として返すことを伝えます
            byte[] body = message.getBytes("UTF-8"); // 返す文字をUTF-8のデータに変換します
            exchange.sendResponseHeaders(200, body.length); // 正常な応答とデータの長さを送ります
            exchange.getResponseBody().write(body); // 変換したデータをブラウザに送ります
            exchange.getResponseBody().close(); // 送信を終えて応答を閉じます
        }); // "/"への処理の登録はここまでです
            server.start(); // サーバーの待ち受けを開始します
        System.out.println("サーバー起動: http://localhost:8080 （止めるときは Ctrl+C）"); // 起動したことをターミナルに表示します
    } // mainメソッドはここまでです
} // Appクラスはここまでです
