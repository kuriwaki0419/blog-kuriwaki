package blog.ex.model.entity;

/*************************************************************************************************************************/
/*                                                                                                                       */
/*                                         ユーザーのブログ情報とDBのユーザーブログ情報の紐づけ                                       */
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
@Data						// クラスに対して、Getter,Setter,toString,equals,hashCodeメソッドを自動生成する
@NoArgsConstructor			// 引数無しのデフォルトコンストラクタを自動生成する
@AllArgsConstructor			// 全ての引数を持つコンストラクタを自動生成する
@RequiredArgsConstructor	// finalなフィールドを初期化する引数付きコンストラクタを自動生成する
@Entity						// エンティティクラスであることを示す
@Table(name = "blog")		// テーブル名の指定をする
public class BlogEntity {

	@Id													// PKであることを示す
	@Column(name = "blog_id")							// フィールド(blogId) と DBのカラム(blog_id)を紐づける
	@GeneratedValue(strategy = GenerationType.AUTO)		// PKを自動生成する方法の指定
	private Long blogId;								// ユーザーが作成するブログのID
	
	@NonNull											// null値が入ることを許容しないことを示す
	@Column(name = "blog_title")						// フィールド(blogTitle) と DBのカラム(blog_title)を紐づける
	private String blogTitle;							// ユーザーが作成するブログのタイトル
	
	@NonNull											// null値が入ることを許容しないことを示す
	@Column(name = "category_name")						// フィールド(categoryName) と DBのカラム(category_name)を紐づける
	private String categoryName;						// ユーザーが作成するブログのカテゴリ
	
	@NonNull											// null値が入ることを許容しないことを示す
	@Column(name = "blog_image")						// フィールド(blogImage) と DBのカラム(blog_image)を紐づける
	private String blogImage;							// ユーザーが作成するブログの画像
	
	@NonNull											// null値が入ることを許容しないことを示す
	@Column(name = "article")							// フィールド(article) と DBのカラム(article)を紐づける
	private String article;								// ユーザーが作成するブログの記事
	
	@Column(name = "account_id")						// フィールド(accountId) と DBのカラム(account_id)を紐づける
	private Long accountId;								// ユーザーの使用するアカウントのID
		
	// 新しいブログ記事のエンティティの作成コンストラクタ -------------------------------------------------------------------------
	public BlogEntity(@NonNull String blogTitle, @NonNull String categoryName, @NonNull String blogImage, 
																		@NonNull String article , Long accountId) {
		this.blogTitle = blogTitle;
		this.categoryName = categoryName;
		this.blogImage = blogImage;
		this.article = article;
		this.accountId = accountId;
	}
	
}
