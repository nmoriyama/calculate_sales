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
		HashMap<String,String> branData = new HashMap<String,String>();

		Map<String,Integer> comSales = new HashMap<String,Integer>();//キー:商品コード , 要素:売上金額
		HashMap<String,String> comData = new HashMap<String,String>();

		int rcdSize = 0;
		try{
			//コマンドライン引数1つじゃない場合
			if(args.length != 1){
				System.out.println("予期せぬエラーが発生しました");
				return;
			}
			if(args[0].endsWith(File.pathSeparator)){
				args[0] = args[0].substring(0,args[0].length()-1);
			}
			//定義ファイルの読み込み
			branData = check(args[0],"branch.lst","支店",3);		 //1支店定義ファイルの読み込み
			if(branData.containsKey("error")){
				System.out.println(branData.get("error"));
				return;
			}
			comData = check(args[0],"commodity.lst","商品",8);		//2商品定義ファイルの読み込み
			if(comData.containsKey("error")){
				System.out.println(comData.get("error"));
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

				//売上ファイル名のフォルダがある場合
				if (!filelist[i].isFile()){
					if (filelist[i].isDirectory()){
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
			}
		//rcdファイル探し終わり
		//ファイル読み込み
			for(int i = 0;i < rcdFileList.size() - rcdSize;i ++){
				File surchFile = new File(args[0] + File.separator + Surch.get(i));
				int num = Integer.parseInt((Surch.get(i).substring(0,8)));
				Num.add(i);
				//連番チェック
				if(Num.size() != num){
					System.out.println("売上ファイル名が連番になっていません");//読み込めなかったら
					return;
				}
				FileReader fileReader = new FileReader(surchFile);
				BufferedReader bufferedReader = new BufferedReader(fileReader);
				try{
					String line;
					int k = 0;
					String surch[] = new String[2];
					while((line = bufferedReader.readLine()) != null){
						dataList.add(line);
						if(k < 2){
							surch[k] = line;//surch[0]に支店コード,surch[1]に商品コード
						}
						k++;
					}
					//３行じゃない場合
					if(k != 3){
						System.out.println(Surch.get(i) + "のフォーマットが不正です");
						return;
					}
					branSales = together("支店",0,i,surch[0],Surch,3,dataList,branSales,branData); //支店ごとの売り上げ
					if(branSales.containsKey(Surch.get(i) + "の支店コードが不正です")){
						System.out.println(Surch.get(i) + "の支店コードが不正です");
						return;
					}else if(branSales.containsKey("合計金額が10桁を超えました")){
						System.out.println("合計金額が10桁を超えました");
						return;
					}
					comSales = together("商品",1,i,surch[1],Surch,8,dataList,comSales,comData); //商品ごとの売り上げ
					if(comSales.containsKey(Surch.get(i) + "の商品コードが不正です")){
						System.out.println(Surch.get(i) + "の商品コードが不正です");
						return;
					}else if(comSales.containsKey("合計金額が10桁を超えました")){
						System.out.println("合計金額が10桁を超えました");
						return;
					}
					dataList.clear();
				}catch(IOException e){
					System.out.println("予期せぬエラーが発生しました");
					return;
				}
				finally{
					bufferedReader.close();
				}
			}
			//4集計結果出力
			print(args[0],"branch.out",branSales,branData);
			print(args[0],"commodity.out",comSales,comData);
		}catch(IOException  e){
			System.out.println("予期せぬエラーが発生しました");
			return;
		}

	}



	//支店定義ファイル,商品定義ファイルの読み込み
	public static HashMap<String,String> check(String place,String fileName,String Name,int size){
		HashMap<String,String> Check = new HashMap<String,String>();
		try{
			File file = new File(place + File.separator + fileName);
			if(!file.exists()){
				Check.put("error",Name + "定義ファイルが存在しません");
				return Check;
			}
			
			FileReader fileReader = new FileReader(file);
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			try{
				String line;
				while((line = bufferedReader.readLine()) != null){
					String[] Data = line.split(",");// , で分割
					Check.put(Data[0],Data[1]);//キー:商品コード , 要素:商品名
					//１行 , が２つ以上多くある もしくは 商品コードが８桁でない 場合
					if(Data.length != 2){
						Check.put("error",Name + "定義ファイルのフォーマットが不正です");
						return Check;
					}
					if (Data[0].length() != size) {//アルファベット、数字のみか
						Check.put("error",Name + "定義ファイルのフォーマットが不正です");
						return Check;
					}
					if(Name == "支店"){
						if (!Data[0].matches("^[0-9]{3}$")) {//数字のみか
							Check.put("error",Name + "定義ファイルのフォーマットが不正です");
							return Check;
						}
					}else if(Name == "商品"){
						if (Data[0].matches("^[0-9]{8}$") || Data[0].matches("^[a-zA-Z]{8}$")) {//アルファベット、数字のみか
							Check.put("error",Name + "定義ファイルのフォーマットが不正です");
							return Check;
						}
					}
				}
			}catch(NumberFormatException e){
				Check.put("error",Name + "定義ファイルのフォーマットが不正です");
				return Check;
			}catch(ArrayIndexOutOfBoundsException e){
				Check.put("error",Name + "定義ファイルのフォーマットが不正です");
				return Check;
			}
			finally{
				bufferedReader.close();
			}
		}catch(IOException  e){
			Check.put("error","予期せぬエラーが発生しました");
			return Check;
		}
		return Check;
	}

	//支店ごと,商品ごとの売り上げ
	public static void print(String place,String name,Map<String,Integer> Sales,HashMap<String,String> Data)throws IOException{
		String crlf = System.getProperty("line.separator");
		//並び替え
		List<Map.Entry<String,Integer>> sort = new ArrayList<Map.Entry<String,Integer>>(Sales.entrySet());
		Collections.sort(sort, new Comparator<Map.Entry<String,Integer>>() {
			public int compare(
			Entry<String,Integer> sort1, Entry<String,Integer> sort2) {
			return ((Integer)sort2.getValue()).compareTo((Integer)sort1.getValue());
			}
			});
		//ここまで
		File file = new File(place + File.separator + name);
		FileWriter fileWriter = new FileWriter(file);
		BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
		for (Entry<String,Integer> s : sort) {
			bufferedWriter.write(s.getKey() + "," + Data.get(s.getKey()) + "," + s.getValue() + crlf);
		}
		bufferedWriter.close();
		return;
	}

	//各合計売上の計算
	public static Map<String,Integer> together(String name,int ｋ,int i,String Code,HashMap<Integer,String> Surch,int size,ArrayList<String> List,Map<String,Integer> Sales,HashMap<String,String> Data){
			long  add = 0;
			if(Code.length() != size || !Data.containsKey(Code)) { //１行目に支店コードがない場合
				Sales.put(Surch.get(i) + "の" + name + "コードが不正です",1000000001);
				return Sales;
			}

			long rcdSales = new Long(List.get(2));//今回取得した売上

			//売上金額格納
			if(Sales.containsKey(Code)){
				add = Sales.get(List.get(ｋ)) + rcdSales;//支店売上
			}else{
				add = rcdSales;//支店売上
			}

			if(add > 1000000000){
				Sales.put("合計金額が10桁を超えました",1000000001);
				return Sales;
			}

			Sales.put(List.get(ｋ),(int)add);
		return Sales;
	}

}

