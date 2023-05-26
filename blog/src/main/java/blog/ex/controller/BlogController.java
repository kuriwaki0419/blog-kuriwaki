package blog.ex.controller;

/********************************************************************************************************************************/
/*                                                                                                                              */
/*                                         画面遷移を制御したり、Service 層(メイン処理)の呼出を行う                                       */
/*                                                                                                                              */
/*                                                                                                                              */
/********************************************************************************************************************************/

// インポート ----------------------------------------------------------------------------------------------------
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import blog.ex.model.entity.BlogEntity;
import blog.ex.model.entity.UserEntity;
import blog.ex.service.BlogService;
import jakarta.servlet.http.HttpSession;

// ユーザーからの入力を View から受け取り、それをもとに Model に指示を伝える処理 -----------------------------------------------
@RequestMapping("/user/blog")									// URLとコントローラーのクラスを紐づける
@Controller														// 画面遷移用のコントローラーに付与する
public class BlogController {

	@Autowired													// 自動でインスタンスの紐づけを行う
	private BlogService blogService;							// ブログの処理を実行するため呼び出す
	
	@Autowired													// 自動でインスタンスの紐づけを行う
	private HttpSession session;								/* 同一のWebブラウザからの複数回のリクエストを、
																	同一のWebブラウザからのアクセスとして処理するため*/
	
	// ブログ画面の表示 -------------------------------------------------------------------------------------------
	@GetMapping("/list")										// HTTP GETリクエストに対する紐づけ
	public String getBlogListPage(Model model) {
		
		// セッションから現在のユーザー情報を取得し、UserEntityのインスタンスが取得出来たら、そのaccountIdを取得する
		UserEntity userList = (UserEntity) session.getAttribute("user");
		Long accountId = userList.getAccountId();
		
		// userListから現在ログインしている人のユーザー名を取得する
		String accountName = userList.getAccountName();
		
		// 現在ログインしているユーザーに関連するブログを取得している
		List<BlogEntity> blogList = blogService.findAllBlogPost(accountId);
		
		// コントローラーからビューに渡すためのデータを格納している
		model.addAttribute("accountName",accountName);
		model.addAttribute("blogList",blogList);
		return "blog-main.html";
	}
	
	// ブログ登録画面の表示 -------------------------------------------------------------------------------------------
	@GetMapping("/register")
	public String getBlogRegisterPage(Model model) {

		// セッションから現在のユーザー情報を取得し、UserEntityのインスタンスが取得出来たら、そのaccountIdを取得する
		UserEntity userList = (UserEntity) session.getAttribute("user");
		Long accountId = userList.getAccountId();
		
		// userListから現在ログインしている人のユーザー名を取得する
		String accountName = userList.getAccountName();

		// コントローラーからビューに渡すためのデータを格納している
		model.addAttribute("accountName", accountName);
		return "blog-register.html";		
	}
	
	// ブログ登録処理 ------------------------------------------------------------------------------------------------
	@PostMapping("/register/process")
	public String blogRegister(@RequestParam String blogTitle, @RequestParam String categoryName,
									@RequestParam("blogImage") MultipartFile blogImage,
									@RequestParam String article, Model model) {
		
		System.out.println(blogTitle);
		System.out.println(categoryName);
		System.out.println(blogImage);
		System.out.println(article);
		
		// セッションから現在のユーザー情報を取得し、UserEntityのインスタンスが取得出来たら、そのaccountIdを取得する
		UserEntity userList = (UserEntity) session.getAttribute("user");
		Long accountId = userList.getAccountId();
		
		// 画像ファイル名を取得する
		String fileName = blogImage.getOriginalFilename();
		
		// ファイルのアップロード処理
		try {
			// 画像ファイルの保存先を指定する
			File blogFile = new File("./src/main/resources/static/blog-img/" + fileName);
			// 画像ファイルからバイナリデータ	を取得する
			byte[] bytes = blogImage.getBytes();
			// 画像を保存(書き出し)するためのバッファを用意する
			BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(blogFile));
			// 画像ファイルの書き出しをする
			out.write(bytes);
			// バッファを閉じることにより、書き出しを正常終了させる
			out.close();
		}catch(Exception e) {
			e.printStackTrace();
		}
		
		// 入力されたデータをデータベースに保存する
		if(blogService.createBlogPost(blogTitle, categoryName, fileName, article, accountId)) {
			return "redirect:/user/blog/list";
		}else {
			model.addAttribute("registerMessage", "既に登録済みです");
			return "blog-register.html";
		}
	}
	
	// ブログ編集画面の表示 -------------------------------------------------------------------------------------------
	@GetMapping("/edit/{blogId}")
	public String getBlogEditPage(@PathVariable Long blogId, Model model) {
		
		// セッションから現在のユーザー情報を取得し、UserEntityのインスタンスが取得出来たら、そのaccountIdを取得する
		UserEntity userList = (UserEntity) session.getAttribute("user");
		Long accountId = userList.getAccountId();
		
		// userListから現在ログインしている人のユーザー名を取得する
		String accountName = userList.getAccountName();
		model.addAttribute("accountName", accountName);
		
		// 指定されたblogIdに対応するブログを取得し、blogListに代入する
		BlogEntity blogList = blogService.getBlogPost(blogId);
		
		// ページ遷移処理
		if(blogList == null) {
			return "redirect:/user/blog/list";
		}else {
			model.addAttribute("blogList", blogList);
//			model.addAttribute("blogList", blogList);
			return "blog-edit.html";				
		}		
	}
	
	// ブログ編集処理 ------------------------------------------------------------------------------------------------
	@PostMapping("/update")
	public String blogUpdate(@RequestParam Long blogId, @RequestParam String blogTitle, @RequestParam String categoryName, 
								@RequestParam String blogImage, @RequestParam String article, Model model) {

		// セッションから現在のユーザー情報を取得し、UserEntityのインスタンスが取得出来たら、そのaccountIdを取得する
		UserEntity userList = (UserEntity) session.getAttribute("user");
		Long accountId = userList.getAccountId();
		
		// 指定されたblogIdに対応するブログを更新する
		if(blogService.editBlogPost(blogId, blogTitle, categoryName, blogImage, article, accountId)) {
			return "blog-main.html";
		}else {
			model.addAttribute("registerMessage", "更新に失敗しました");
			return "blog-edit.html";
		}
	}

}
