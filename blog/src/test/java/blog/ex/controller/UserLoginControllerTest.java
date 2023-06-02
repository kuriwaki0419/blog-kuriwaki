package blog.ex.controller;

/********************************************************************************************************************************/
/*                                                                                                                              */
/*                                                      ユーザーログイン処理のテスト                                                    */
/*                                                                                                                              */
/*                                                                                                                              */
/********************************************************************************************************************************/

// インポート ----------------------------------------------------------------------------------------------------------------
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import blog.ex.model.entity.UserEntity;
import blog.ex.service.UserService;
import jakarta.servlet.http.HttpSession;

// テスト処理 ----------------------------------------------------------------------------------------------------------------
@SpringBootTest						// Spring Bootアプリケーションのコンテキストをロードしてテストを実行するための注釈
@AutoConfigureMockMvc				// Spring MVCテストのためにMockMvcを自動的に設定するための注釈
public class UserLoginControllerTest {

	@Autowired
	private MockMvc mockMvc;		// テスト対象のコントローラと対話するためのモックされたMVCモックオブジェクト
	
	@MockBean						// モックオブジェクトを作成して注入するための注釈 
	private UserService userService;
	
	
	// オブジェクトの設定とモックの設定 ----------------------------------------------------------------------------------------------
	@BeforeEach						// 各テストメソッドの実行前に実行されるメソッドを示す。つまり、各テストケースの前に共通の前準備を行うために使用される。
	public void prepareData() {
		
		// UserEntityオブジェクトを作成
		UserEntity userEntity = new UserEntity(1L, "test", "test@gmail.com", "pass");
		
		// userServiceのloginAccountメソッドの引数が"test"と"pass"の場合にuserEntityを返すようにモック化される
		when(userService.loginAccount(eq("test"), eq("pass"))).thenReturn(userEntity);
		
		// 異なる引数でもモックが設定される
		when(userService.loginAccount(eq("test"), eq("tttt"))).thenReturn(null);
		when(userService.loginAccount(eq("eeee"), eq("pass"))).thenReturn(null);
		when(userService.loginAccount(eq(""), eq("pass"))).thenReturn(null);
		when(userService.loginAccount(eq("test"), eq(""))).thenReturn(null);
		when(userService.loginAccount(eq(""), eq(""))).thenReturn(null);
		when(userService.loginAccount(eq("eeee"), eq("tttt"))).thenReturn(null);
	}
	
	// ログインページの取得検証 --------------------------------------------------------------------------------------------------
	@Test
	public void accessLoginPage_Succeed() throws Exception{
		
		// /user/loginへのgetリクエストの作成
		RequestBuilder request = MockMvcRequestBuilders.get("/user/login");
		
		// ビュー名が"login.html"であることを検証
		mockMvc.perform(request).andExpect(view().name("login.html"));
	}
	
	/********************************************************************************************************************/
	/*            正常なログインの成功を検証(正しいユーザー名とパスワードが提供された場合にログインが成功し、リダイレクトが正しく行われる)             */
	/********************************************************************************************************************/	
	@Test
	public void testLogin_Successful() throws Exception {
		
		// /user/login/processへのPOSTリクエストを作成する
		RequestBuilder request = MockMvcRequestBuilders.post("/user/login/process").param("accountName", "test").param("password", "pass");
		
		// リクエストを実行する(andExpect(redirectedUrl("/user/blog/list"))を使用してリダイレクト先のURLが"/user/blog/list"であることを検証する。)
		MvcResult result = mockMvc.perform(request).andExpect(redirectedUrl("/user/blog/list")).andReturn();
		
		// セッションを取得する
		HttpSession session = result.getRequest().getSession();
		
		// セッションからログインユーザーエンティティを取得する
		UserEntity loggedInUser = (UserEntity)session.getAttribute("user");
		assertNotNull(loggedInUser);											// ログインユーザーエンティティがnullでないことを検証する
		assertEquals("test", loggedInUser.getAccountName());					// ログインユーザーエンティティのユーザー名が正しいことを検証する
		assertEquals("test@gmail.com", loggedInUser.getAccountEmail());			// ログインユーザーエンティティのEmailが正しいことを検証する
	}
	
	/********************************************************************************************************************/
	/*                                 ログインの失敗を検証(パスワードが間違っている場合にログインが失敗する)                            */
	/********************************************************************************************************************/	
	@Test
	public void testLogin_WrongPassword_Unsuccessful() throws Exception {
		
		// /user/login/processへのPOSTリクエストを作成する
		RequestBuilder request = MockMvcRequestBuilders.post("/user/login/process").param("accountName", "test").param("password", "tttt");
		
		// リクエストを実行するandExpect(status().is3xxRedirection()).andExpect(redirectedUrl("/user/login")を使用してリダイレクトが正しく行われることを検証する。)
		mockMvc.perform(request).andExpect(status().is3xxRedirection()).andExpect(redirectedUrl("/user/login"));

		// セッションを取得する
		HttpSession session = mockMvc.perform(MockMvcRequestBuilders.get("/user/login")).andReturn().getRequest().getSession();

		// セッションからログインユーザーエンティティを取得する
		UserEntity loggedInUser = (UserEntity) session.getAttribute("user");
		assertNull(loggedInUser);			// ログインユーザーエンティティがnullであることを検証する
	}
	
	/********************************************************************************************************************/
	/*                                 ログインの失敗を検証(ユーザー名が間違っている場合にログインが失敗する)                            */
	/********************************************************************************************************************/	
	@Test
	public void testLogin_WrongAccountName_Unsuccessful() throws Exception {

		// /user/login/processへのPOSTリクエストを作成する
		RequestBuilder request = MockMvcRequestBuilders.post("/user/login/process").param("accountName", "eeee").param("password", "pass");
		
		// リクエストを実行するandExpect(status().is3xxRedirection()).andExpect(redirectedUrl("/user/login")を使用してリダイレクトが正しく行われることを検証する。)
		mockMvc.perform(request).andExpect(status().is3xxRedirection()).andExpect(redirectedUrl("/user/login"));

		// セッションを取得する
		HttpSession session = mockMvc.perform(MockMvcRequestBuilders.get("/user/login")).andReturn().getRequest().getSession();

		// セッションからログインユーザーエンティティを取得する
		UserEntity loggedInUser = (UserEntity) session.getAttribute("user");
		assertNull(loggedInUser);			// ログインユーザーエンティティがnullであることを検証する
	}

	/********************************************************************************************************************/
	/*                              ログインの失敗を検証(ユーザー名とパスワードが間違っている場合にログインが失敗する)                       */
	/********************************************************************************************************************/	
	@Test
	public void testLogin_WrongAccountNameAndWrongPassword_Unsuccessful() throws Exception {
		
		// /user/login/processへのPOSTリクエストを作成する
		RequestBuilder request = MockMvcRequestBuilders.post("/user/login/process").param("accountName", "eeee").param("password", "tttt");
		
		// リクエストを実行するandExpect(status().is3xxRedirection()).andExpect(redirectedUrl("/user/login")を使用してリダイレクトが正しく行われることを検証する。)
		mockMvc.perform(request).andExpect(status().is3xxRedirection()).andExpect(redirectedUrl("/user/login"));

		// セッションを取得する
		HttpSession session = mockMvc.perform(MockMvcRequestBuilders.get("/user/login")).andReturn().getRequest().getSession();

		// セッションからログインユーザーエンティティを取得する
		UserEntity loggedInUser = (UserEntity) session.getAttribute("user");
		assertNull(loggedInUser);			// ログインユーザーエンティティがnullであることを検証する
	}

	/********************************************************************************************************************/
	/*                                   ログインの失敗を検証(パスワードが空欄の場合にログインが失敗する)                              */
	/********************************************************************************************************************/	
	@Test
	public void testLogin_NullPassword_Unsuccessful() throws Exception {
		
		// /user/login/processへのPOSTリクエストを作成する
		RequestBuilder request = MockMvcRequestBuilders.post("/user/login/process").param("accountName", "test").param("password", "");
		
		// リクエストを実行するandExpect(status().is3xxRedirection()).andExpect(redirectedUrl("/user/login")を使用してリダイレクトが正しく行われることを検証する。)
		mockMvc.perform(request).andExpect(status().is3xxRedirection()).andExpect(redirectedUrl("/user/login"));

		// セッションを取得する
		HttpSession session = mockMvc.perform(MockMvcRequestBuilders.get("/user/login")).andReturn().getRequest().getSession();

		// セッションからログインユーザーエンティティを取得する
		UserEntity loggedInUser = (UserEntity) session.getAttribute("user");
		assertNull(loggedInUser);			// ログインユーザーエンティティがnullであることを検証する
	}
	
	/********************************************************************************************************************/
	/*                                   ログインの失敗を検証(ユーザー名が空欄の場合にログインが失敗する)                              */
	/********************************************************************************************************************/	
	@Test
	public void testLogin_NullAccountName_Unsuccessful() throws Exception {

		// /user/login/processへのPOSTリクエストを作成する
		RequestBuilder request = MockMvcRequestBuilders.post("/user/login/process").param("accountName", "").param("password", "pass");
		
		// リクエストを実行するandExpect(status().is3xxRedirection()).andExpect(redirectedUrl("/user/login")を使用してリダイレクトが正しく行われることを検証する。)
		mockMvc.perform(request).andExpect(status().is3xxRedirection()).andExpect(redirectedUrl("/user/login"));

		// セッションを取得する
		HttpSession session = mockMvc.perform(MockMvcRequestBuilders.get("/user/login")).andReturn().getRequest().getSession();

		// セッションからログインユーザーエンティティを取得する
		UserEntity loggedInUser = (UserEntity) session.getAttribute("user");
		assertNull(loggedInUser);			// ログインユーザーエンティティがnullであることを検証する
	}
	
	/********************************************************************************************************************/
	/*                                 ログインの失敗を検証(ユーザー名とパスワードが空欄の場合にログインが失敗する)                        */
	/********************************************************************************************************************/	
	@Test
	public void testLogin_NullAccountNameAndNullPassword_Unsuccessful() throws Exception {
		
		// /user/login/processへのPOSTリクエストを作成する
		RequestBuilder request = MockMvcRequestBuilders.post("/user/login/process").param("accountName", "").param("password", "");
		
		// リクエストを実行するandExpect(status().is3xxRedirection()).andExpect(redirectedUrl("/user/login")を使用してリダイレクトが正しく行われることを検証する。)
		mockMvc.perform(request).andExpect(status().is3xxRedirection()).andExpect(redirectedUrl("/user/login"));

		// セッションを取得する
		HttpSession session = mockMvc.perform(MockMvcRequestBuilders.get("/user/login")).andReturn().getRequest().getSession();

		// セッションからログインユーザーエンティティを取得する
		UserEntity loggedInUser = (UserEntity) session.getAttribute("user");
		assertNull(loggedInUser);			// ログインユーザーエンティティがnullであることを検証する
	}

}
