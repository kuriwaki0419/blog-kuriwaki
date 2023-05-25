package blog.ex.controller;


/********************************************************************************************************************************/
/*                                                                                                                              */
/*                                         画面遷移を制御したり、Service 層(メイン処理)の呼出を行う                                       */
/*                                                                                                                              */
/*                                                                                                                              */
/********************************************************************************************************************************/

// インポート ----------------------------------------------------------------------------------------------------
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import blog.ex.service.UserService;

// ユーザーからの入力を View から受け取り、それをもとに Model に指示を伝える処理 -----------------------------------------------
@RequestMapping("/user")											// URLとコントローラーのクラスを紐づける
@Controller															// 画面遷移用のコントローラーに付与する
public class UserRegisterController {

	@Autowired														// 自動でインスタンスの紐づけを行う
	private UserService userService;								// 新規登録の処理を実行するため呼び出す
	
	// アカウント新規登録画面の表示 ----------------------------------------------------------------------------------
	@GetMapping("/register")										// HTTP GETリクエストに対する紐づけ
	public String getUserRegisterPage() {
		return "register.html";
	}
	
	// アカウント新規登録処理 ----------------------------------------------------------------------------------------
	@PostMapping("/register/process")
	public String register(@RequestParam String accountName, @RequestParam String accountEmail, @RequestParam String password) {
		System.out.println(accountName);
		System.out.println(accountEmail);
		System.out.println(password);
		// アカウント作成メソッドの呼び出し処理		
		if(userService.createAccount(accountName, accountEmail, password)) {
			return "redirect:/user/login";
		}else {
			return "register.html";
		}
	}
}
