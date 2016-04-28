package jp.co.plusize.moriyama_naoki.calculate_sales;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class calculate_sales {
	public static void main(String[] args){
		ArrayList<String> dataList = new ArrayList<String>();
		ArrayList<String> rcdFileList = new ArrayList<String>();
		ArrayList<Integer> Num = new ArrayList<Integer>();
		
		HashMap<Integer,String> Surch = new HashMap<Integer,String>();
		
		Map<String,Integer> branSales = new HashMap<String,Integer>();//キー:支店コード , 要素:売上金額
		HashMap<String,String> branName = new HashMap<String,String>();
		HashMap<Integer,String> branCode = new HashMap<Integer,String>();
		HashMap<String,Integer> branCodeSales = new HashMap<String,Integer>();
		
		Map<String,Integer> comSales = new HashMap<String,Integer>();//キー:商品コード , 要素:売上金額
		HashMap<String,String> comName = new HashMap<String,String>();
		HashMap<Integer,String> comCode = new HashMap<Integer,String>();
		HashMap<String,Integer> comCodeSales = new HashMap<String,Integer>();

		int rcdSize = 0;
		
		//1支店定義ファイルの読み込み
		try{
			//コマンドライン引数がない場合
			if(args.length == 0){
				System.out.println("予期せぬエラーが発生しました");
				return;
			}
			if(args.length > 1){
				System.out.println("予期せぬエラーが発生しました");
				return;
			}
			
			if(args[0].endsWith(File.pathSeparator)){
				args[0] = args[0].substring(0,args[0].length()-1);
			}
			File file = new File(args[0] + File.separator +"branch.lst");
			
			if(!file.exists()){
				System.out.println("支店定義ファイルが存在しません");
				return;
			}
			
			FileReader fileReader = new FileReader(file);
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			
			try{
				String line;
				int count = 0;
				while((line = bufferedReader.readLine()) != null){
					String[] branch = line.split(",");// , で分割
					branName.put(branch[0],branch[1]);//キー:支店コード , 要素:支店名
					branCode.put(count,branch[0]);//キー:０～ , 要素：支店コード
					//１行 , が２つ以上多くある もしくは 支店コードが３桁でない 場合
					if(branch.length != 2 ){ 
						System.out.println("支店定義ファイルのフォーマットが不正です");
						return;
					}
					if (!branch[0].matches("^[0-9]{3}$") || branch[0].length() != 3) {//数字のみか
						System.out.println("支店定義ファイルのフォーマットが不正です");
						return;
					}
					count++;
				}
			}catch(NumberFormatException e){
				System.out.println("支店定義ファイルのフォーマットが不正です");
				return;
			}catch(ArrayIndexOutOfBoundsException e){
				System.out.println("支店定義ファイルのフォーマットが不正です");
				return;
			}
			finally {
				bufferedReader.close();
			}
		}catch(IOException  e){
			System.out.println("予期せぬエラーが発生しました");
			return;
		}
		
		
		//2商品定義ファイルの読み込み
		try{ 
			File file = new File(args[0] + File.separator + "commodity.lst");
			
			if(!file.exists()){
				System.out.println("商品定義ファイルが存在しません");
				return;
			}
			
			FileReader fileReader = new FileReader(file);
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			
			try{
				String line;
				int count = 0;
				while((line = bufferedReader.readLine()) != null){
					String[] commodity = line.split(",");// , で分割
					comName.put(commodity[0],commodity[1]);//キー:商品コード , 要素:商品名
					comCode.put(count, commodity[0]);//キー:０～ , 要素：商品コード
					//１行 , が２つ以上多くある もしくは 商品コードが８桁でない 場合
					if(commodity.length != 2){
						System.out.println("商品定義ファイルのフォーマットが不正です");
						return;
					}
					if (!commodity[0].matches("^[0-9a-zA-Z]{8}$") || commodity[0].length() != 8) {//アルファベット、数字のみか
						System.out.println("商品定義ファイルのフォーマットが不正です");
						return;
					}
					count++;
				}
			}catch(NumberFormatException e){
				System.out.println("商品定義ファイルのフォーマットが不正です");
				return;
			}catch(ArrayIndexOutOfBoundsException e){
				System.out.println("商品定義ファイルのフォーマットが不正です");
				return;
			}
			finally{
				bufferedReader.close();
			}
		}catch(IOException  e){
			System.out.println("予期せぬエラーが発生しました");
			System.out.println(e);
			return;
		}

		
		//3集計
		//売上ファイルのある場所の検索
		File file = new File(args[0]);
		
		String[] rcdSurch = file.list();
		for(int i = 0;i < rcdSurch.length;i ++){
			String line = rcdSurch[i];
			
			File[] filelist = file.listFiles();
			String surch = "0";
			if (filelist[i].isFile()){
				// ファイルだった時の処理
			}else if (filelist[i].isDirectory()){
				String surchNum = String.valueOf(i+2);
				while(surch.length() < (8 - surchNum.length())){
					surch += "0";
				}
				surch = surch.concat(surchNum);
				File surchFile = new File(args[0] + File.separator + surch + ".rcd");
				if(!surchFile.exists()){
					//見つからなかったら
					rcdSize = 1;
					i++;
				}else{
					//見つかったら
					System.out.println("売上ファイル名が連番になっていません");
					return;
				}
			}
			//拡張子がrcd かつ 12桁(８桁+拡張子(.rcd))
			if(line.endsWith("rcd") && line.length() == 12){
				Integer.parseInt(line.substring(0,8));//rcdファイルが数字かどうか
				Surch.put(i,line);
				rcdFileList.add(line);
			}else if(!(line.endsWith("lst") || line.endsWith("out") || line.endsWith("java"))){				
				System.out.println("売上ファイル名が連番になっていません");
				return;
			}
		}//rcdファイル探し終わり

		
		//ファイル読み込み
		try{
			for(int i = 0;i < rcdFileList.size() - rcdSize;i ++){
				File surchFile = new File(args[0] + File.separator + Surch.get(i));
				int num = Integer.parseInt((Surch.get(i).substring(0,8)));
				Num.add(i);
				//連番チェック
				if(Num.size() == num){
				}else{	//読み込めなかったら
					System.out.println("売上ファイル名が連番になっていません");
					return;
					
				}
				
				FileReader fileReader = new FileReader(surchFile);
				BufferedReader bufferedReader = new BufferedReader(fileReader);
				
				try{
					long branchSales = 0,comoditySales = 0;
					String line;
					
					int k = 0;
					String surch[] = new String[2];
					while((line = bufferedReader.readLine()) != null){
						dataList.add(line);
						if(k < 2){
							surch[k] = line;
						}
						k++;
					}
					//３行じゃない場合
					if(k != 3){
						System.out.println(Surch.get(i) + "のフォーマットが不正です");	
						return;
					}
					//支店コード、商品コードの確認
					if(surch[0].length() != 3 || !branName.containsKey(surch[0])) { //１行目に支店コードがない場合
						System.out.println(Surch.get(i) + "の支店コードが不正です");
						return;
					}else if(surch[1].length() != 8 || !comName.containsKey(surch[1])){//２行目に商品コードがない場合	
						System.out.println(Surch.get(i) + "の商品コードが不正です");	
						return;
					}
					long rcdSales = new Long(dataList.get(2));//今回取得した売上
					//売上金額格納
					if(branSales.containsKey(surch[0])){
						branchSales = branSales.get(dataList.get(0)) + rcdSales;//支店売上
					}else{
						branchSales = rcdSales;//支店売上
					}
					if(comSales.containsKey(surch[1])){
						comoditySales = comSales.get(dataList.get(1)) + rcdSales;//商品売上
					}else{
						comoditySales =  rcdSales;//商品売上
					}
						
					if(branchSales > 1000000000 || comoditySales > 1000000000){
						System.out.println("合計金額が10桁を超えました");
						return;
					}
					
					branSales.put(dataList.get(0),(int)branchSales);
					branCodeSales.put(dataList.get(0),(int)branchSales);//支店コードと売上
 
					comSales.put(dataList.get(1),(int)comoditySales);
					comCodeSales.put(dataList.get(1),(int)comoditySales);//商品コードと売上
					dataList.clear();
					
				}catch(IOException e){
					System.out.println("予期せぬエラーが発生しました");
					return;
				}
				finally{
					bufferedReader.close();				
				}
			}
			
		}catch(IOException  e){
			System.out.println("予期せぬエラーが発生しました");
			return;
		}
		
		//4集計結果出力		
		try{
			//並び替え
			List<Map.Entry<String,Integer>> sort = new ArrayList<Map.Entry<String,Integer>>(branSales.entrySet());
	        Collections.sort(sort, new Comparator<Map.Entry<String,Integer>>() {
	        public int compare(
	            Entry<String,Integer> sort1, Entry<String,Integer> sort2) {
	        	return ((Integer)sort2.getValue()).compareTo((Integer)sort1.getValue());
	        }
	        });
			//ここまで
	        File branchOut = new File(args[0] + File.separator + "branch.out");
			//branchOut.createNewFile();
			FileWriter fileWriter = new FileWriter(branchOut);
			BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);	
			try{
				for (Entry<String,Integer> s : sort) {
		            bufferedWriter.write(s.getKey() + "," + branName.get(s.getKey()) + "," + s.getValue() + "\r\n");
		        }
			}catch(IOException  e){
				//branchOut.deleteOnExit() ;
				System.out.println("予期せぬエラーが発生しました");
				return;
			}
			finally{
				bufferedWriter.close();
			}
			
		}catch(IOException  e){
			System.out.println("予期せぬエラーが発生しました");
			return;
		}
	
		try{		
			List<Map.Entry<String,Integer>> sort = new ArrayList<Map.Entry<String,Integer>>(comSales.entrySet());
	        Collections.sort(sort, new Comparator<Map.Entry<String,Integer>>() {
	        public int compare(
	            Entry<String,Integer> sort1, Entry<String,Integer> sort2) {
	        	return ((Integer)sort2.getValue()).compareTo((Integer)sort1.getValue());
	        }});
			File comout = new File(args[0] + File.separator + "commodity.out");
			//comout.createNewFile();
			FileWriter fileWriter = new FileWriter(comout);
			BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
			try{
				for (Entry<String,Integer> s : sort) {
		            bufferedWriter.write(s.getKey() + "," + comName.get(s.getKey()) + "," + s.getValue() + "\r\n");
		        }
			}catch(IOException e){		
				//comout.deleteOnExit() ;
				System.out.println("予期せぬエラーが発生しました");
				return;
			}	
			finally{
				bufferedWriter.close();
			}
		}catch(IOException  e){
			System.out.println("予期せぬエラーが発生しました");
			return;
		}
	}
}
