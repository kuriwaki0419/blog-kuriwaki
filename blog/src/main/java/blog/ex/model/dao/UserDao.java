package blog.ex.model.dao;

/****************************************************************************************************************************************/
/*                                                                                                                                      */
/*                                                 エンティティのCRUD操作を簡単に実装するためのインターフェース                                         */
/*                                                                                                                                      */
/*                                                                                                                                      */
/****************************************************************************************************************************************/

// インポート ------------------------------------------------------------------------------------------------------------------------
import org.springframework.data.jpa.repository.JpaRepository;
import blog.ex.model.entity.UserEntity;

// CRUD操作を簡単に実装するための処理 ---------------------------------------------------------------------------------------------------
public interface UserDao extends JpaRepository<UserEntity, Long> {

	UserEntity save(UserEntity userEntity);					// UserEntityを引数として受け取り、UserEntityを保存、保存したUserEntityを返す
	
	UserEntity findByAccountEmail(String accountEmail);		// String型の引数を受け取り、受け取った引数と一致するaccountEmailを持つUserEntityを返す
	
	UserEntity findByAccountNameAndPassword(String accountName, String password);	// 引数で受け取ったNameとpasswordをUserEntityの中で一致するものを探す
}
