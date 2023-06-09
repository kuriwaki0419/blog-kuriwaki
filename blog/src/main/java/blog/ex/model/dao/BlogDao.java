package blog.ex.model.dao;

/****************************************************************************************************************************************/
/*                                                                                                                                      */
/*                           	              エンティティのCRUD操作を簡単に実装するためのインターフェース                                              */
/*                                                                                                                                      */
/*                                                                                                                                      */
/****************************************************************************************************************************************/

// インポート ------------------------------------------------------------------------------------------------------------------------
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import blog.ex.model.entity.BlogEntity;
import jakarta.transaction.Transactional;

// CRUD操作を簡単に実装するための処理 ---------------------------------------------------------------------------------------------------
public interface BlogDao extends JpaRepository<BlogEntity, Long> {

	List<BlogEntity> findByAccountId(Long accountId);		// accountIdに一致する複数のBlogEntityを取得する
	
	BlogEntity save(BlogEntity blogEntity);					// 引数で渡されたBlogEntityオブジェクトをDBに格納する
	
	// ブログのタイトルとカテゴリから検索して、BlogEntityオブジェクトを取得する
	BlogEntity findByBlogTitleAndCategoryName(String blogTitle, String categoryName);
	
	// blogIdに一致する複数のBlogEntityを取得する
	BlogEntity findByBlogId(Long blogId);
	
	// BlogEntityのIDに基づいて、該当するBlogEntityを削除する
	@Transactional
	List<BlogEntity> deleteByBlogId(Long blogId);
	
	//キーワードから検索
	List<BlogEntity> findByBlogTitleContainingOrCategoryNameContaining(String titleKeyword, String categoryKeyword);
}
