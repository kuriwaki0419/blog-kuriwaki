package blog.ex.model.entity;

/*************************************************************************************************************************/
/*                                                                                                                       */
/*                                         ユーザーの情報とDBのユーザー情報の紐づけ                                               */
/*                                                                                                                       */
/*                                                                                                                       */
/*************************************************************************************************************************/

// インポート ----------------------------------------------------------------------------------------------------
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

// 紐づけ処理 ---------------------------------------------------------------------------------------------------
@Data							// クラスに対して、Getter,Setter,toString,equals,hashCodeメソッドを自動生成する
@NoArgsConstructor				// 引数無しのデフォルトコンストラクタを自動生成する
@AllArgsConstructor				// 全ての引数を持つコンストラクタを自動生成する
@RequiredArgsConstructor		// finalなフィールドを初期化する引数付きコンストラクタを自動生成する
@Entity							// エンティティクラスであることを示す
@Table(name="account")			// テーブル名の指定をする
public class UserEntity {
	
	@Id													// PKであることを示す
	@Column(name = "account_id")						// フィールド(accountId) と DBのカラム(account_id)を紐づける
	@GeneratedValue(strategy = GenerationType.AUTO)		// PKを自動生成する方法の指定
	private Long accountId;								// ユーザーの使用するアカウントのID
	
	@NonNull											// null値が入ることを許容しないことを示す
	@Column(name = "account_name")						// フィールド(accountName) と DBのカラム(account_name)を紐づける
	private String accountName;							// ユーザーの使用するアカウントの名前
	
	@NonNull											// null値が入ることを許容しないことを示す
	@Column(name = "account_email")						// フィールド(accountEmail) と DBのカラム(account_email)を紐づける
	private String accountEmail;						// ユーザーの使用するアカウントのメール
	
	@NonNull											// null値が入ることを許容しないことを示す
	@Column(name = "password")							// フィールド(accountEmail) と DBのカラム(account_email)を紐づける
	private String password;							// ユーザーの使用するアカウントのパスワード
}
