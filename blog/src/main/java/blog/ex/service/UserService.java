package blog.ex.service;

/****************************************************************************************************************************************/
/*                                                                                                                                      */
/*                                                 ユーザーの操作に対するロジック、アルゴリズム                                                      */
/*                                                                                                                                      */
/*                                                                                                                                      */
/****************************************************************************************************************************************/

// インポート ------------------------------------------------------------------------------------------------------------------------
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import blog.ex.model.dao.UserDao;
import blog.ex.model.entity.UserEntity;

// ユーザー操作処理 ------------------------------------------------------------------------------------------------------------------
@Service 						// カプセル化された状態でモデル内に単独で存在するインターフェースとして提供される操作として最初に定義されたことを示す
public class UserService {

	@Autowired					// 自動でインスタンスの紐づけを行う
	private UserDao userDao;	// accountテーブルにアクセスして操作するため
	
	/***************************************************************************************/
	/*                            ユーザーアカウント作成メソッド                                    */
	/***************************************************************************************/	
	public boolean createAccount(String accountName, String accountEmail, String password) {
		
		// 指定されたemailを検索してuserEntityに格納する
		UserEntity userEntity = userDao.findByAccountEmail(accountEmail);

		// emailが見つからなかった場合、アカウントの作成を行う
		if(userEntity == null) {
			userDao.save(new UserEntity(accountName,accountEmail,password));
			return true;
		}else {
			return false;
		}
	}
	
	/***************************************************************************************/
	/*                            ユーザーアカウントログインメソッド                                   */
	/***************************************************************************************/	
	public UserEntity loginAccount(String accountName, String password) {

		// 指定されたアカウント名とパスワードが一致するかを検索してuserEntityに格納する
		UserEntity userEntity = userDao.findByAccountNameAndPassword(accountName, password);
		
		// 検索した結果見つからなかった場合、null を返す
		if(userEntity == null) {
			return null ;
		}else {
			return userEntity;	// 見つかった場合はログインに成功したことを示すために、userEntityを返す
		}
	}
}
