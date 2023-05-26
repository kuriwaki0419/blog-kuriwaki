package blog.ex.service;

/****************************************************************************************************************************************/
/*                                                                                                                                      */
/*                                                 ブログの操作に対するロジック、アルゴリズム                                                      */
/*                                                                                                                                      */
/*                                                                                                                                      */
/****************************************************************************************************************************************/

// インポート ------------------------------------------------------------------------------------------------------------------------
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import blog.ex.model.dao.BlogDao;
import blog.ex.model.entity.BlogEntity;

// ブログ操作処理 ------------------------------------------------------------------------------------------------------------------
@Service						// カプセル化された状態でモデル内に単独で存在するインターフェースとして提供される操作として最初に定義されたことを示す
public class BlogService {

	@Autowired					// 自動でインスタンスの紐づけを行う
	private BlogDao blogDao;	// accountテーブルにアクセスして操作するため
		
	/***************************************************************************************/
	/*                            全てのブログを取得するメソッド                                    */
	/***************************************************************************************/	
	public List<BlogEntity> findAllBlogPost(Long accountId){
		
		// 指定されたaccountIdに対応する全てのブログを取得し、BlogEntityオブジェクトのリストとして返す
		if(accountId == null) {
			return null ;
		}else {
			return blogDao.findByAccountId(accountId);
		}
	}
	
	/***************************************************************************************/
	/*                       既存のブログか調べて、新規ブログを作成するメソッド                         */
	/***************************************************************************************/	
	public boolean createBlogPost(String blogTitle, String categoryName, String blogImage, String article , Long accountId) {
		
		// 既に同じタイトルとカテゴリが存在するのかを検索する
		BlogEntity blogList = blogDao.findByBlogTitleAndCategoryName(blogTitle, categoryName);
		
		// もし、ブログが存在しなければ以下の処理を実行する
		if(blogList == null) {
			
			// 新しいブログが作成され、trueを返す
			blogDao.save(new BlogEntity(blogTitle,categoryName,blogImage,article,accountId));
			return true;
		}else {
			
			// 既にブログが存在していた場合はfalseを返す
			return false;
		}
	}
	
	/***************************************************************************************/
	/*                   受け取った引数に該当するBlogEntityを取得して返すメソッド                     */
	/***************************************************************************************/	
	public BlogEntity getBlogPost(Long blogId) {
		if(blogId == null) {
			return null;
		}else {
			return blogDao.findByBlogId(blogId);
		}
	}
	
	/***************************************************************************************/
	/*                                     ブログ記事の更新処理                                */
	/***************************************************************************************/	
	public boolean editBlogPost(Long blogId, String blogTitle, String categoryName, String blogImage, String article , Long accountId) {
		BlogEntity blogList = blogDao.findByBlogId(blogId);
		if(accountId == null) {
			return false;
		}else {
			blogList.setBlogId(blogId);
			blogList.setBlogTitle(blogTitle);
			blogList.setCategoryName(categoryName);
			blogList.setBlogImage(blogImage);
			blogList.setArticle(article);
			blogList.setAccountId(accountId);
			blogDao.save(blogList);
			return true;
		}
	}
}
