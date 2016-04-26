//package jp.co.plusize.moriyama_naoki.calculate_sales;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;

public class calculate_sales {
	public static void main(String[] args){
		ArrayList<String> dataList = new ArrayList<String>();
		ArrayList<String> fileList = new ArrayList<String>();
		
		HashMap<String,Integer> branchmap1 = new HashMap<String,Integer>();
		HashMap<String,String> branchmap2 = new HashMap<String,String>();
		HashMap<String,Integer> branchmap3 = new HashMap<String,Integer>();
		HashMap<String,Integer> commap1 = new HashMap<String,Integer>();
		HashMap<String,String> commap2 = new HashMap<String,String>();
		HashMap<String,Integer> commap3 = new HashMap<String,Integer>();
		
		TreeMap<Integer,String> BranchList = new TreeMap<Integer,String>();
		TreeMap<Integer,String> ComList = new TreeMap<Integer,String>();
		int x = 0,y=0,sumdata1 = 0,sumdata2 = 0,s3;
		String er;
		
		try{//1支店定義ファイルの読み込み
			File file1 = new File(args[0] + "\\branch.lst");
			if(!file1.exists()){
				throw new Exception("支店定義ファイルが存在しません");
			}
			FileReader fr1 = new FileReader(file1);
			BufferedReader br1 = new BufferedReader(fr1);

			String s1;
			
			while((s1 = br1.readLine()) != null){
				System.out.println(s1);
				String[] branch = s1.split(",");// , で分割
				branchmap1.put(branch[0],0);//キー:支店コード , 要素:売上金額
				branchmap2.put(branch[0],branch[1]);//キー:支店コード , 要素:支店名
				//１行 , が２つ以上多くある もしくは 支店コードが３桁でない 場合
				if(branch.length > 2 || branch[0].length() != 3){ 
					br1.close();
					throw new 	Exception("支店定義ファイルのフォーマットが不正です");
				}
			}
			
			br1.close();
		}catch(	Exception  e){
			System.out.println(e);
		}
		
		try{ //2商品定義ファイルの読み込み
			File file2 = new File(args[0] + "\\commodity.lst");
			if(!file2.exists()){
				throw new Exception("商品定義ファイルが存在しません");
			}
			FileReader fr2 = new FileReader(file2);
			BufferedReader br2 = new BufferedReader(fr2);
			String s2;

			while((s2 = br2.readLine()) != null){
				String[] commodity = s2.split(",");// , で分割
				commap1.put(commodity[0],0);//キー:商品コード , 要素:売上金額
				commap2.put(commodity[0],commodity[1]);//キー:商品コード , 要素:商品名
				//１行 , が２つ以上多くある もしくは 商品コードが８桁でない 場合
				if(commodity.length > 2 || commodity[0].length() != 8){
					br2.close();
					throw new IllegalArgumentException ("商品定義ファイルのフォーマットが不正です");
				}
			}
			br2.close();
		}catch(Exception  e){
			System.out.println(e);
		}

		//3集計
		//売上ファイルのある場所の検索
		File file3 = new File(args[0]);
		String[] files = file3.list();
		for(int i=0;i<files.length;i++){
			String xfiles = files[i];

			//拡張子がrcd かつ 12桁(８桁+拡張子(.rcd))
			if(xfiles.endsWith("rcd") && xfiles.length() == 12){
				Integer.parseInt(xfiles.substring(0,8));//rcdファイルが数字かどうか
				fileList.add(xfiles);
			}
		}//rcdファイル探し終わり

		//ファイル読み込み
		x=1;
		for(int i=0;i<fileList.size();i++){
			String X = String.valueOf(x);
			String s2 = "0";
			while(s2.length() < (8 - X.length())){
				s2 += "0";
			}
			s2 = s2.concat(X);//s2にファイル名

			try{
				File file4 = new File(args[0] + "\\" + s2 + ".rcd");
				//連番チェック
				if(!file4.exists()){//読み込めなかったら
					X = String.valueOf(x+1);
					s2 = "0";
					while(s2.length() < (8 - X.length())){
						s2 += "0";
					}
					s2 = s2.concat(X);
					file4 = new File(args[0] + "\\" + s2 + ".rcd");
					if(!file4.exists()){//読み込めなかった場合
						throw new 	IOException("予期せぬエラーが発生しました");
					}else{//連番じゃなかった場合
						throw new 	IllegalArgumentException("売上ファイル名が連番になっていません");
					}
				}		
				FileReader fr4 = new FileReader(file4);
				BufferedReader br4 = new BufferedReader(fr4);
				String s4;
				er = s2;
				while((s4 = br4.readLine()) != null){
					dataList.add(s4);
					if(y == 0){ //１行目に支店コードがない場合
						if(!branchmap1.containsKey(s4)){
							br4.close();
							throw new 	IllegalArgumentException (er + ".rcdの支店コードが不正です");
						}
					}else if(y == 1){//２行目に商品コードがない場合
						if(!commap1.containsKey(s4)){
							br4.close();
							throw new IllegalArgumentException(er + ".rcdの商品コードが不正です");	
						}
					}
					y++;
				}
				//４行以上の場合
				
				if(y != 3){
					br4.close();
					throw new 	IllegalArgumentException(er + ".rcdのフォーマットが不正です");	
				}
				y = 0;
				
				int sum2 = Integer.parseInt(dataList.get(2));//今回取得した売上
				sumdata1 = branchmap1.get(dataList.get(0)) + sum2;//支店売上
				sumdata2 = commap1.get(dataList.get(1)) + sum2;//商品売上
				
				if(sumdata1 >= 1000000000 || sumdata2 >= 1000000000){
					br4.close();
					throw new IllegalArgumentException("合計金額が10桁を超えました");
				}
				branchmap1.put(dataList.get(0),sumdata1);
				branchmap3.put(dataList.get(0),sumdata1);//支店コードと売上
 
				commap1.put(dataList.get(1),sumdata2);
				commap3.put(dataList.get(1),sumdata2);//商品コードと売上
				x++;
				dataList.clear();
				br4.close();
			}catch(IllegalArgumentException e){
				System.out.println(e);
			}catch(IOException e){
				System.out.println("予期せぬエラーが発生しました");
			}
		}
		
		try{
			//4集計結果出力
			File file5 = new File(args[0] + "\\branch.out");
			FileWriter fw5 = new FileWriter(file5);
			BufferedWriter bw5 = new BufferedWriter(fw5);	
			
			x=1;
			for(int i = 0;i < branchmap1.size();i++){
				String X = String.valueOf(x);
				String s2 = "0";
				while(s2.length() < (3-X.length())){
					s2 += "0";
				}
				s2 = s2.concat(X);//支店コード
				if(branchmap3.get(s2) == null){
					branchmap3.put(s2,0);
				}
				BranchList.put(branchmap3.get(s2),s2);//treemap にキー:売上金額 要素:支店コード
				x++;
			}
			//書き込み
			System.out.println("BranchList.firstKey　　："+ BranchList.firstKey());
			System.out.println("BranchList.get　　:" + BranchList.get(0));
			System.out.println("branchmap2.get　　；" + branchmap2.get(005));
			System.out.println("branchmap2　　；" + branchmap2);
			System.out.println(branchmap2.get(BranchList.get(BranchList.firstKey())));
			bw5.write(BranchList.get(BranchList.firstKey()) + "," + branchmap2.get(BranchList.get(BranchList.firstKey())) + "," + BranchList.firstKey() + "\r\n");
			s3 = BranchList.firstKey();
			while(BranchList.higherKey(s3) != null){
				bw5.write(BranchList.get(BranchList.higherKey(s3)) + "," + branchmap2.get(BranchList.get(BranchList.higherKey(s3))) + "," + BranchList.higherKey(s3) + "\r\n");
				s3 = BranchList.higherKey(s3);
			}
			bw5.close();
			
			File file6 = new File(args[0] + "\\commodity.out");
			FileWriter fw6 = new FileWriter(file6);
			BufferedWriter bw6 = new BufferedWriter(fw6);
			x = 1;
			 
			for(int i=0;i<commap1.size();i++){
				 
				String X = String.valueOf(x);
				String s2 = "SFT0";
				 
				while(s2.length()<(8-X.length())){
					s2+="0";
				}
				s2=s2.concat(X);//商品コード
				if(commap3.get(s2) == null){
					commap3.put(s2,0);
				}
				ComList.put(commap3.get(s2),s2);//treemap にキー:売上金額 要素:商品コード
				x++;
			}
			//書き込み
			bw6.write(ComList.get(ComList.firstKey()) + "," + commap2.get(ComList.get(ComList.firstKey())) + "," + ComList.firstKey() + "\r\n");
			s3 = ComList.firstKey();
			while(ComList.higherKey(s3) != null){
				bw6.write(ComList.get(ComList.higherKey(s3)) + "," + commap2.get(ComList.get(ComList.higherKey(s3))) + "," + ComList.higherKey(s3) + "\r\n");
				s3 = ComList.higherKey(s3);
			}
			bw6.close();
		}catch(IOException e){
			System.out.println("予期せぬエラーが発生しました");
		}	
	}
}