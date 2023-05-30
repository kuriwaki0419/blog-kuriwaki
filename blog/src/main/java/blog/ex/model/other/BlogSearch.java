package blog.ex.model.other;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data						// クラスに対して、Getter,Setter,toString,equals,hashCodeメソッドを自動生成する
@NoArgsConstructor			// 引数無しのデフォルトコンストラクタを自動生成する
@AllArgsConstructor			// 全ての引数を持つコンストラクタを自動生成する
public class BlogSearch {

	private int searchFlg;
	private String searchKeywords;
	
}
