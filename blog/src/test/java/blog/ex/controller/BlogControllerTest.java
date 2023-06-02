package blog.ex.controller;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/********************************************************************************************************************************/
/*                                                                                                                              */
/*                                                    　　　 ブログ処理のテスト     　　　　                                               */
/*                                                                                                                              */
/*                                                                                                                              */
/********************************************************************************************************************************/

//　インポート ------------------------------------------------------------------------------------------------------------------------
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import blog.ex.model.entity.BlogEntity;
import blog.ex.model.entity.UserEntity;
import blog.ex.service.BlogService;

//　テスト処理 ----------------------------------------------------------------------------------------------------------------
@SpringBootTest						// Spring Bootアプリケーションのコンテキストをロードしてテストを実行するための注釈
@AutoConfigureMockMvc				// Spring MVCテストのためにMockMvcを自動的に設定するための注釈
public class BlogControllerTest {

	@Autowired
	private MockMvc mockMvc;		 // テスト対象のコントローラと対話するためのモックされたMVCモックオブジェクト
	
	@MockBean						 // モックオブジェクトを作成して注入するための注釈 
	private BlogService blogService;
	
	private MockHttpSession session; // SpringBootのユニットテストでセッションスコープのテストをしたいときに MockHttpSession を使用することでモックデータを作成ができる。
	
	// オブジェクトの設定とモックの設定 ----------------------------------------------------------------------------------------------
	@BeforeEach						 // 各テストメソッドの実行前に実行されるメソッドを示す。つまり、各テストケースの前に共通の前準備を行うために使用される。
	private void prepareData() {
		
		// UserEntityオブジェクトを作成
		UserEntity user = new UserEntity();
		user.setAccountId(1L);				// テストに必要な値をセットする(AccountId)
		user.setAccountName("test");		// テストに必要な値をセットする(AccountName)
		
		// ブログ記事のインスタンス作成(記事の数分)
		List<BlogEntity> blogList = new ArrayList<>();
		blogList.add(new BlogEntity());
		blogList.add(new BlogEntity());
		blogList.add(new BlogEntity());
		blogList.add(new BlogEntity());
		blogList.add(new BlogEntity());
		blogList.add(new BlogEntity());
		blogList.add(new BlogEntity());
				
		session = new MockHttpSession();	// HttpSessionを模倣している
		session.setAttribute("user", user);	// 上記で設定した値を(userを基に)sessionとして取り扱う
		
		// 一覧表示の成功と失敗の処理
		when(blogService.findAllBlogPost(1L)).thenReturn(blogList);		// 一覧表示成功
		when(blogService.findAllBlogPost(null)).thenReturn(null);		// 一覧表示失敗
	}
	
	/********************************************************************************************************************/
	/*                                ブログ画面の表示テスト1(アカウントに紐づけられた記事がある場合)                                  */
	/********************************************************************************************************************/	
	@Test
	public void testGetBlogListPage_BlogArticle_Success() throws Exception {

		// /user/login/listへのPOSTリクエストを作成する
		RequestBuilder request = MockMvcRequestBuilders.get("/user/blog/list").session(session);
		
		// mockMvcを使用してリクエストを実行する。
		mockMvc.perform(request)
		.andExpect(status().isOk())
		.andExpect(view().name("blog-main.html"))
		.andExpect(model().attributeExists("accountName", "blogList"))
		.andExpect(model().attribute("accountName", "test"));
	}
	
	/********************************************************************************************************************/
	/*                                ブログ画面の表示テスト2(アカウントに紐づけられた記事が無い場合)                                  */
	/********************************************************************************************************************/	
	@Test
	public void testGetBlogListPage_NoBlogArticle_Success() throws Exception {

		// UserEntityオブジェクトを作成
		UserEntity user = new UserEntity();
		user.setAccountId(1L);				// テストに必要な値をセットする(AccountId)
		user.setAccountName("test2");		// テストに必要な値をセットする(AccountName)
		
		session = new MockHttpSession();	// HttpSessionを模倣している
		session.setAttribute("user", user);	// 上記で設定した値を(userを基に)sessionとして取り扱う
		
		// /user/login/listへのPOSTリクエストを作成する
		RequestBuilder request = MockMvcRequestBuilders.get("/user/blog/list").session(session);
		
		// ブログの記事のインスタンス作成(何も入っていない場合)
		List<BlogEntity> nullBlogList = new ArrayList<>();
		when(blogService.findAllBlogPost(1L)).thenReturn(nullBlogList);	// 何も記事が入っていないListを返している
		
		// mockMvcを使用してリクエストを実行する。
		mockMvc.perform(request)
		.andExpect(status().isOk())
		.andExpect(view().name("blog-main.html"))
		.andExpect(model().attributeExists("accountName", "blogList"))
		.andExpect(model().attribute("accountName", "test2"))
		.andExpect(model().attribute("blogList", nullBlogList));
	}

	/********************************************************************************************************************/
	/*                                              ブログ登録画面の表示テスト                                                */
	/********************************************************************************************************************/	
	@Test
	public void testGetRegisterPage() throws Exception {
		
		// mockMvcを使用してGETリクエストを実行する(セッション情報もリクエストに含まれる)
		mockMvc.perform(get("/user/blog/register").session(session))
		.andExpect(status().isOk())													// レスポンスのHTTPステータスが200(OK)であることを検証する
		.andExpect(view().name("blog-register.html"))								// レスポンスのビュー名が "blog-register.html" であることを検証する
		.andExpect(model().attributeExists("accountName"));							/* レスポンスのモデルに "accountName" と 
																								"registerMessage" という属性が存在することを検証する。(BlogController,139行目参照)*/
	}
	
	/********************************************************************************************************************/
	/*                                 正常にブログが登録されるテスト(全パラメーター既存のブログと被りなし)                                */
	/********************************************************************************************************************/	
	@Test
	public void testBlogRegister_Succeed() throws Exception {
		
		// mockの動作設定
		when(blogService.createBlogPost(anyString(), anyString(), anyString(), anyString(), anyLong())).thenReturn(true);

		// ラストデータの準備
		MockMultipartFile blogImage = new MockMultipartFile("blogImage", "demo画面.png", "image/png", new byte[0] ); // 画像のセット
		
		// テスト実行(テスト用のパラメーターを渡してあげる)
		mockMvc.perform(MockMvcRequestBuilders.multipart("/user/blog/register/process")
				.file(blogImage)
				.param("blogTitle", "testEx")
				.param("categoryName", "testCategory")
				.param("article", "testArticle")
				.session(session))
				.andExpect(status().is3xxRedirection()) 	  // リダイレクトを期待する
		        .andExpect(redirectedUrl("/user/blog/list")); // リダイレクト先のURLを指定する
		
		// mockのメソッドが正しく呼ばれたことを検証
		verify(blogService, times(1)).createBlogPost(eq("testEx"),eq("testCategory"), eq("demo画面.png"), eq("testArticle"), eq(1L));

	}
	
	/********************************************************************************************************************/
	/*                               正常にブログが登録されるテスト(タイトルのみ既存のブログと同じ物を使用)                               */
	/********************************************************************************************************************/	
	@Test
	public void testBlogRegister_ExistingBlogTitle_Succeed() throws Exception {
		
		// mockの動作設定
		when(blogService.createBlogPost(anyString(), anyString(), anyString(), anyString(), anyLong())).thenReturn(true);

		// ラストデータの準備
		MockMultipartFile blogImage = new MockMultipartFile("blogImage", "demo画面.png", "image/png", new byte[0] ); // 画像のセット
		
		// テスト実行(テスト用のパラメーターを渡してあげる)
		mockMvc.perform(MockMvcRequestBuilders.multipart("/user/blog/register/process")
				.file(blogImage)
				.param("blogTitle", "demo")					// 既存のブログのタイトルと被り
				.param("categoryName", "testCategory")
				.param("article", "testArticle")
				.session(session))
				.andExpect(status().is3xxRedirection()) 	  // リダイレクトを期待する
		        .andExpect(redirectedUrl("/user/blog/list")); // リダイレクト先のURLを指定する
		
		// mockのメソッドが正しく呼ばれたことを検証
		verify(blogService, times(1)).createBlogPost(eq("demo"), eq("testCategory"), eq("demo画面.png"), eq("testArticle"), eq(1L));
	}
	
	/********************************************************************************************************************/
	/*                               正常にブログが登録されるテスト(カテゴリーのみ既存のブログと同じ物を使用)                               */
	/********************************************************************************************************************/	
	@Test
	public void testBlogRegister_ExistingCategoryName_Succeed() throws Exception {
		
		// mockの動作設定
		when(blogService.createBlogPost(anyString(), anyString(), anyString(), anyString(), anyLong())).thenReturn(true);

		// ラストデータの準備
		MockMultipartFile blogImage = new MockMultipartFile("blogImage", "demo画面.png", "image/png", new byte[0] ); // 画像のセット
		
		// テスト実行(テスト用のパラメーターを渡してあげる)
		mockMvc.perform(MockMvcRequestBuilders.multipart("/user/blog/register/process")
				.file(blogImage)
				.param("blogTitle", "testEx")
				.param("categoryName", "demoCategory")		// 既存のブログのカテゴリと被り
				.param("article", "testArticle")
				.session(session))
				.andExpect(status().is3xxRedirection()) 	  // リダイレクトを期待する
		        .andExpect(redirectedUrl("/user/blog/list")); // リダイレクト先のURLを指定する
		
		// mockのメソッドが正しく呼ばれたことを検証
		verify(blogService, times(1)).createBlogPost(eq("testEx"), eq("demoCategory"), eq("demo画面.png"), eq("testArticle"), eq(1L));
	}

	/********************************************************************************************************************/
	/*                               正常にブログが登録されるテスト(記事のみ既存のブログと同じ物を使用)                                 */
	/********************************************************************************************************************/	
	@Test
	public void testBlogRegister_ExistingArticle_Succeed() throws Exception {
		
		// mockの動作設定
		when(blogService.createBlogPost(anyString(), anyString(), anyString(), anyString(), anyLong())).thenReturn(true);

		// ラストデータの準備
		MockMultipartFile blogImage = new MockMultipartFile("blogImage", "demo画面.png", "image/png", new byte[0] ); // 画像のセット
		
		// テスト実行(テスト用のパラメーターを渡してあげる)
		mockMvc.perform(MockMvcRequestBuilders.multipart("/user/blog/register/process")
				.file(blogImage)
				.param("blogTitle", "testEx")
				.param("categoryName", "testCategory")
				.param("article", "demoArticle")			// 既存のブログの記事と被り
				.session(session))
				.andExpect(status().is3xxRedirection()) 	  // リダイレクトを期待する
		        .andExpect(redirectedUrl("/user/blog/list")); // リダイレクト先のURLを指定する
		
		// mockのメソッドが正しく呼ばれたことを検証
		verify(blogService, times(1)).createBlogPost(eq("testEx"), eq("testCategory"), eq("demo画面.png"), eq("demoArticle"), eq(1L));
	}

	/********************************************************************************************************************/
	/*                                   ブログの登録が失敗するテスト(全パラメーター既存のブログと被り)                                  */
	/********************************************************************************************************************/	
	@Test
	public void testBlogRegister_Failure() throws Exception {
		
		// mockの動作設定
		when(blogService.createBlogPost(anyString(), anyString(), anyString(), anyString(), anyLong())).thenReturn(false);
		
		// ラストデータの準備
		MockMultipartFile blogImage = new MockMultipartFile("blogImage", "demo画面.png", "image/png", new byte[0]);	// 画像のセット
		
		// テスト実行(テスト用のパラメーターを渡してあげる)
		mockMvc.perform(MockMvcRequestBuilders.multipart("/user/blog/register/process")
				.file(blogImage)
				.param("blogTitle", "demo")					// 既存のブログのタイトルと被り
				.param("categoryName", "demoCategory")		// 既存のブログのカテゴリと被り
				.param("article", "demoArticle")			// 既存のブログの記事と被り
				.session(session))
				.andExpect(status().isOk())
				.andExpect(view().name("blog-register.html"))
				.andExpect(model().attributeExists("registerMessage"));
		
		// mockのメソッドが正しく呼ばれたことを検証
		verify(blogService, times(1)).createBlogPost(eq("demo"),eq("demoCategory"), eq("demo画面.png"), eq("demoArticle"), eq(1L));
	}

	/********************************************************************************************************************/
	/*                                      ブログの登録が失敗するテスト(タイトルが空白になっている場合)                               */
	/********************************************************************************************************************/	
	@Test
	public void testBlogRegister_NullBlogTitle_Failure() throws Exception {
		
		// mockの動作設定
		when(blogService.createBlogPost(anyString(), anyString(), anyString(), anyString(), anyLong())).thenReturn(false);
		
		// ラストデータの準備
		MockMultipartFile blogImage = new MockMultipartFile("blogImage", "demo画面.png", "image/png", new byte[0]);	// 画像のセット
		
		// テスト実行(テスト用のパラメーターを渡してあげる)
		mockMvc.perform(MockMvcRequestBuilders.multipart("/user/blog/register/process")
				.file(blogImage)
				.param("blogTitle", "")						// タイトルが空白
				.param("categoryName", "testCategory")
				.param("article", "testArticle")
				.session(session))
				.andExpect(status().isOk())
				.andExpect(view().name("blog-register.html"))
				.andExpect(model().attributeExists("registerMessage"));
		
		// mockのメソッドが正しく呼ばれたことを検証
		verify(blogService, times(1)).createBlogPost(eq(""),eq("testCategory"), eq("demo画面.png"), eq("testArticle"), eq(1L));
	}

	/********************************************************************************************************************/
	/*                                      ブログの登録が失敗するテスト(カテゴリが空白になっている場合)                               */
	/********************************************************************************************************************/	
	@Test
	public void testBlogRegister_NullCategoryName_Failure() throws Exception {
		
		// mockの動作設定
		when(blogService.createBlogPost(anyString(), anyString(), anyString(), anyString(), anyLong())).thenReturn(false);
		
		// ラストデータの準備
		MockMultipartFile blogImage = new MockMultipartFile("blogImage", "demo画面.png", "image/png", new byte[0]);	// 画像のセット
		
		// テスト実行(テスト用のパラメーターを渡してあげる)
		mockMvc.perform(MockMvcRequestBuilders.multipart("/user/blog/register/process")
				.file(blogImage)
				.param("blogTitle", "testEx")
				.param("categoryName", "")					// カテゴリが空白
				.param("article", "testArticle")
				.session(session))
				.andExpect(status().isOk())
				.andExpect(view().name("blog-register.html"))
				.andExpect(model().attributeExists("registerMessage"));
		
		// mockのメソッドが正しく呼ばれたことを検証
		verify(blogService, times(1)).createBlogPost(eq("testEx"),eq(""), eq("demo画面.png"), eq("testArticle"), eq(1L));
	}
	
	/********************************************************************************************************************/
	/*                                      ブログの登録が失敗するテスト(記事が空白になっている場合)                                 */
	/********************************************************************************************************************/	
	@Test
	public void testBlogRegister_NullArticle_Failure() throws Exception {
		
		// mockの動作設定
		when(blogService.createBlogPost(anyString(), anyString(), anyString(), anyString(), anyLong())).thenReturn(false);
		
		// ラストデータの準備
		MockMultipartFile blogImage = new MockMultipartFile("blogImage", "demo画面.png", "image/png", new byte[0]);	// 画像のセット
		
		// テスト実行(テスト用のパラメーターを渡してあげる)
		mockMvc.perform(MockMvcRequestBuilders.multipart("/user/blog/register/process")
				.file(blogImage)
				.param("blogTitle", "testEx")
				.param("categoryName", "testCategory")
				.param("article", "")						// 記事が空白
				.session(session))
				.andExpect(status().isOk())
				.andExpect(view().name("blog-register.html"))
				.andExpect(model().attributeExists("registerMessage"));
		
		// mockのメソッドが正しく呼ばれたことを検証
		verify(blogService, times(1)).createBlogPost(eq("testEx"),eq("testCategory"), eq("demo画面.png"), eq(""), eq(1L));
	}

}
