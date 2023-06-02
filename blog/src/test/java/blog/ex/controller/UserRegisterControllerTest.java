package blog.ex.controller;


/********************************************************************************************************************************/
/*                                                                                                                              */
/*                                                  アカウント登録処理のテスト                                                         */
/*                                                                                                                              */
/*                                                                                                                              */
/********************************************************************************************************************************/

// インポート ----------------------------------------------------------------------------------------------------------------
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import blog.ex.service.UserService;

// テスト処理 ----------------------------------------------------------------------------------------------------------------
@SpringBootTest						// Spring Bootアプリケーションのコンテキストをロードしてテストを実行するための注釈
@AutoConfigureMockMvc				// Spring MVCテストのためにMockMvcを自動的に設定するための注釈
public class UserRegisterControllerTest {

	@Autowired
	private MockMvc mockMvc;		// テスト対象のコントローラと対話するためのモックされたMVCモックオブジェクト
	
	@MockBean						// モックオブジェクトを作成して注入するための注釈 
	private UserService userService;

	// オブジェクトの設定とモックの設定 ----------------------------------------------------------------------------------------------
	@BeforeEach						// 各テストメソッドの実行前に実行されるメソッドを示す。つまり、各テストケースの前に共通の前準備を行うために使用される。
	public void prepareData() {
		
		// userServiceのcreateAccountメソッドの引数で既存のEmail以外であり、空欄がないならtrueを返すようにモック化される
		when(userService.createAccount(eq("test4"), eq("test4@gmail.com"), eq("pass4"))).thenReturn(true);
		when(userService.createAccount(eq("test"), eq("test4@gmail.com"), eq("pass4"))).thenReturn(true);
		when(userService.createAccount(eq("test4"), eq("test@gmail.com"), eq("pass4"))).thenReturn(false);
		when(userService.createAccount(eq("test4"), eq("test4@gmail.com"), eq("pass"))).thenReturn(true);
		when(userService.createAccount(eq(""), eq("test4@gmail.com"), eq("pass4"))).thenReturn(false);
		when(userService.createAccount(eq("test4"), eq(""), eq("pass4"))).thenReturn(false);
		when(userService.createAccount(eq("test4"), eq("test4@gmail.com"), eq(""))).thenReturn(false);
		when(userService.createAccount(eq("test"), eq("test@gmail.com"), eq("pass"))).thenReturn(false);
	}
	
	// アカウント登録ページの取得検証 ----------------------------------------------------------------------------------------------
	@Test
	public void testGetUserRegisterPage() throws Exception {

		// /user/registerへのgetリクエストの作成
		RequestBuilder request = MockMvcRequestBuilders.get("/user/register");

		// ビュー名が"register.html"であることを検証
		mockMvc.perform(request).andExpect(view().name("register.html"));
	}
	
	/********************************************************************************************************************/
	/*     正常なアカウント登録の成功を検証(既存のEmail以外で、空欄がないパラメーターを提供された場合にログインが成功し、リダイレクトが正しく行われる     */
	/********************************************************************************************************************/	
	@Test
	public void testRegister_Successful() throws Exception {
		RequestBuilder request = MockMvcRequestBuilders.post("/user/register/process").param("accountName", "test4")
																.param("accountEmail", "test4@gmail.com").param("password", "pass4");
		
		// リクエストパラメーターを元にリクエストを実行
		mockMvc.perform(request).andExpect(redirectedUrl("/user/login"));
		
		// userServiceのcreateAccountメソッドが指定された引数で1回呼び出されたことを検証する
		verify(userService, times(1)).createAccount(eq("test4"), eq("test4@gmail.com"), eq("pass4"));
	}
	
	/********************************************************************************************************************/
	/*       正常なアカウント登録の成功を検証(名前のみ既存で、空欄がないパラメーターを提供された場合にログインが成功し、リダイレクトが正しく行われる      */
	/********************************************************************************************************************/	
	@Test
	public void testRegister_ExistingName_Successful() throws Exception {
		RequestBuilder request = MockMvcRequestBuilders.post("/user/register/process").param("accountName", "test")
																.param("accountEmail", "test4@gmail.com").param("password", "pass4");
		
		// リクエストパラメーターを元にリクエストを実行
		mockMvc.perform(request).andExpect(redirectedUrl("/user/login"));
		
		// userServiceのcreateAccountメソッドが指定された引数で1回呼び出されたことを検証する
		verify(userService, times(1)).createAccount(eq("test"), eq("test4@gmail.com"), eq("pass4"));
	}

	/********************************************************************************************************************/
	/*     正常なアカウント登録の成功を検証(パスワードのみ既存で、空欄がないパラメーターを提供された場合にログインが成功し、リダイレクトが正しく行われる     */
	/********************************************************************************************************************/	
	@Test
	public void testRegister_ExistingPassword_Successful() throws Exception {
		RequestBuilder request = MockMvcRequestBuilders.post("/user/register/process").param("accountName", "test4")
																.param("accountEmail", "test4@gmail.com").param("password", "pass");
		
		// リクエストパラメーターを元にリクエストを実行
		mockMvc.perform(request).andExpect(redirectedUrl("/user/login"));
		
		// userServiceのcreateAccountメソッドが指定された引数で1回呼び出されたことを検証する
		verify(userService, times(1)).createAccount(eq("test4"), eq("test4@gmail.com"), eq("pass"));
	}
	
	/********************************************************************************************************************/
	/*                  アカウント登録の失敗を検証(既に存在するアカウントのパラメーターを渡した場合にアカウントの登録が失敗する)                   */
	/********************************************************************************************************************/	
	@Test
	public void testRegister_ExistingUser_Unsuccessful() throws Exception {
		RequestBuilder request = MockMvcRequestBuilders.post("/user/register/process").param("accountName", "test")
																.param("accountEmail", "test@gmail.com").param("password", "pass");
		
		// リクエストパラメーターを元にリクエストを実行
		mockMvc.perform(request).andExpect(view().name("register.html"));

		// userServiceのcreateAccountメソッドが指定された引数で1回呼び出されたことを検証する
		verify(userService, times(1)).createAccount(eq("test"), eq("test@gmail.com"), eq("pass"));
	}

	/********************************************************************************************************************/
	/*               アカウント登録の失敗を検証(Emailのみ既存で、空欄がないパラメーターを提供された場合アカウントの登録は失敗する)               */
	/********************************************************************************************************************/	
	@Test
	public void testRegister_ExistingAccountEmail_Unsuccessful() throws Exception {
		RequestBuilder request = MockMvcRequestBuilders.post("/user/register/process").param("accountName", "test4")
																.param("accountEmail", "test@gmail.com").param("password", "pass4");
		
		// リクエストパラメーターを元にリクエストを実行
		mockMvc.perform(request).andExpect(view().name("register.html"));

		// userServiceのcreateAccountメソッドが指定された引数で1回呼び出されたことを検証する
		verify(userService, times(1)).createAccount(eq("test4"), eq("test@gmail.com"), eq("pass4"));
	}
	
	/********************************************************************************************************************/
	/*                     アカウント登録の失敗を検証(アカウント名がnullのパラメーターを渡した場合にアカウントの登録が失敗する)                  */
	/********************************************************************************************************************/	
	@Test
	public void testRegister_NullAccountName_Unsuccessful() throws Exception {
		RequestBuilder request = MockMvcRequestBuilders.post("/user/register/process").param("accountName", "")
																.param("accountEmail", "test4@gmail.com").param("password", "pass4");
		
		// リクエストパラメーターを元にリクエストを実行
		mockMvc.perform(request).andExpect(view().name("register.html"));

		// userServiceのcreateAccountメソッドが指定された引数で1回呼び出されたことを検証する
		verify(userService, times(1)).createAccount(eq(""), eq("test4@gmail.com"), eq("pass4"));
	}

	/********************************************************************************************************************/
	/*                      アカウント登録の失敗を検証(Emailがnullのパラメーターを渡した場合にアカウントの登録が失敗する)                    */
	/********************************************************************************************************************/	
	@Test
	public void testRegister_NullAccountEmail_Unsuccessful() throws Exception {
		RequestBuilder request = MockMvcRequestBuilders.post("/user/register/process").param("accountName", "test4")
																.param("accountEmail", "").param("password", "pass4");
		
		// リクエストパラメーターを元にリクエストを実行
		mockMvc.perform(request).andExpect(view().name("register.html"));

		// userServiceのcreateAccountメソッドが指定された引数で1回呼び出されたことを検証する
		verify(userService, times(1)).createAccount(eq("test4"), eq(""), eq("pass4"));
	}

	/********************************************************************************************************************/
	/*                     アカウント登録の失敗を検証(パスワードがnullのパラメーターを渡した場合にアカウントの登録が失敗する)                   */
	/********************************************************************************************************************/	
	@Test
	public void testRegister_NullPassword_Unsuccessful() throws Exception {
		RequestBuilder request = MockMvcRequestBuilders.post("/user/register/process").param("accountName", "test4")
																.param("accountEmail", "test4@gmail.com").param("password", "");
		
		// リクエストパラメーターを元にリクエストを実行
		mockMvc.perform(request).andExpect(view().name("register.html"));

		// userServiceのcreateAccountメソッドが指定された引数で1回呼び出されたことを検証する
		verify(userService, times(1)).createAccount(eq("test4"), eq("test4@gmail.com"), eq(""));
	}
	
	/********************************************************************************************************************/
	/*                     アカウント登録の失敗を検証(パスワードがnullのパラメーターを渡した場合にアカウントの登録が失敗する)                   */
	/********************************************************************************************************************/	
	@Test
	public void testRegister_NullAccountNameAndAccountEmailAndPassword_Unsuccessful() throws Exception {
		RequestBuilder request = MockMvcRequestBuilders.post("/user/register/process").param("accountName", "")
																.param("accountEmail", "").param("password", "");
		
		// リクエストパラメーターを元にリクエストを実行
		mockMvc.perform(request).andExpect(view().name("register.html"));

		// userServiceのcreateAccountメソッドが指定された引数で1回呼び出されたことを検証する
		verify(userService, times(1)).createAccount(eq(""), eq(""), eq(""));
	}

}
