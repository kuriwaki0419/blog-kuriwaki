package blog.ex.controller;

/********************************************************************************************************************************/
/*                                                                                                                              */
/*                                         画面遷移を制御したり、Service 層(メイン処理)の呼出を行う                                       */
/*                                                                                                                              */
/*                                                                                                                              */
/********************************************************************************************************************************/

//インポート ----------------------------------------------------------------------------------------------------
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import blog.ex.model.entity.UserEntity;
import blog.ex.service.UserService;
import jakarta.servlet.http.HttpSession;

//ユーザーからの入力を View から受け取り、それをもとに Model に指示を伝える処理 -----------------------------------------------
@RequestMapping("/user")										// URLとコントローラーのクラスを紐づける
@Controller														// 画面遷移用のコントローラーに付与する
public class UserLoginController {
	
	@Autowired													// 自動でインスタンスの紐づけを行う
	private UserService userService;							// ログインの処理を実行するため呼び出す
	
	@Autowired													// 自動でインスタンスの紐づけを行う
	private HttpSession session;								/* 同一のWebブラウザからの複数回のリクエストを、
																	同一のWebブラウザからのアクセスとして処理するため*/
	
	// アカウントログイン画面の表示 -----------------------------------------------------------------------------------
	@GetMapping("/login")													// HTTP GETリクエストに対する紐づけ
	public String getUserLoginPage() {
		return "login.html";
	}
	
	// アカウントログイン処理 ----------------------------------------------------------------------------------------
	@PostMapping("/login/process")				// POSTリクエストのみをURLとコントローラーのクラスまたはメソッドを紐づけることができる
	public String login(@RequestParam String accountName, @RequestParam String password) {

		// ログインできるかチェックした結果をuserListに格納
		UserEntity userList = userService.loginAccount(accountName, password);
		if(userList == null) {
			return "redirect:/user/login";
		}else {
			session.setAttribute("user",userList);	// セッションにログイン情報を格納する
			return "redirect:/user/blog/list";
		}
	}

}
