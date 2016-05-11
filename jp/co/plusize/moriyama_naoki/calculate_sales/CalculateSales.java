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

public class CalculateSales {
	public static void main(String[] args){
		ArrayList<String> valueTemp = new ArrayList<String>();
		ArrayList<String> rcdFileList = new ArrayList<String>();
		ArrayList<Integer> sequenceCheck = new ArrayList<Integer>();
		
		Map<String,Integer> branchSales = new HashMap<String,Integer>();//キー:支店コード , 要素:売上金額
		Map<String,String> branchName = new HashMap<String,String>();

		Map<String,Integer> commoditySales = new HashMap<String,Integer>();//キー:商品コード , 要素:売上金額
		Map<String,String> commodityName = new HashMap<String,String>();
		String[] branch = new String[2];
		branch[0] = "branch.lst";
		branch[1] = "branch.out";
		String[] commodity = new String[2];
		commodity[0] = "commodity.lst";
		commodity[1] = "commodity.out";
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
			branchName = fileScan(args[0],branch[0],"支店");		 //1支店定義ファイルの読み込み//支店コードをbranSalesに
			if(branchName.containsKey("error")){
				System.out.println(branchName.get("error"));
				return;
			}
			for (String getCode : branchName.keySet()) {
				branchSales.put(getCode,0);
			}
			commodityName = fileScan(args[0],commodity[0],"商品");		//2商品定義ファイルの読み込み
			if(commodityName.containsKey("error")){
				System.out.println(commodityName.get("error"));
				return;
			}
			for (String getCode : commodityName.keySet()) {
				commoditySales.put(getCode,0);
			}
			//3集計
			//売上ファイルのある場所の検索
			File file = new File(args[0]);
			String[] allFile = file.list();//探す場所にあるファイル全部
			File[] checkDirectory = file.listFiles();
			for(int i = 0;i < allFile.length;i ++){
				String line = allFile[i];
				if (!checkDirectory[i].isDirectory()){
					if(line.matches("^[0-9]{8}.rcd$")){	//8桁の数字.rcdか				
						rcdFileList.add(line);	
					}
				}
			}
		//ファイル読み込み
			for(int i = 0;i < rcdFileList.size();i ++){
				File surchFile = new File(args[0] + File.separator + rcdFileList.get(i));
				int rcdInteger = Integer.parseInt(rcdFileList.get(i).substring(0,8)); //rcdファイル名を数字に
				sequenceCheck.add(i);//読み込めたらsizeが一つ増える
				//連番チェック
				if(sequenceCheck.size() != rcdInteger){
					System.out.println("売上ファイル名が連番になっていません");//読み込めなかったら
					return;
				}
				FileReader fileReader = new FileReader(surchFile);
				BufferedReader bufferedReader = new BufferedReader(fileReader);
				try{
					String line;
					int j = 0;
					String surch[] = new String[2];
					while((line = bufferedReader.readLine()) != null){
						valueTemp.add(line);
						if(j < 2){
							surch[j] = line;//surch[0]に支店コード,surch[1]に商品コード
						}
						j++;
					}
					if(j != 3){					//３行じゃない場合
						System.out.println(rcdFileList.get(i) + "のフォーマットが不正です");
						return;
					}
					branchSales = aggregate("支店",0,surch[0],rcdFileList.get(i),3,valueTemp,branchSales,branchName); //支店ごとの売り上げ
					if(branchSales.containsKey(rcdFileList.get(i) + "の支店コードが不正です")){
						System.out.println(rcdFileList.get(i) + "の支店コードが不正です");
						return;
					}else if(branchSales.containsKey("合計金額が10桁を超えました")){
						System.out.println("合計金額が10桁を超えました");
						return;
					}
					commoditySales = aggregate("商品",1,surch[1],rcdFileList.get(i),8,valueTemp,commoditySales,commodityName); //商品ごとの売り上げ
					if(commoditySales.containsKey(rcdFileList.get(i) + "の商品コードが不正です")){
						System.out.println(rcdFileList.get(i) + "の商品コードが不正です");
						return;
					}else if(commoditySales.containsKey("合計金額が10桁を超えました")){
						System.out.println("合計金額が10桁を超えました");
						return;
					}
					valueTemp.clear();
				}catch(IOException e){
					System.out.println("予期せぬエラーが発生しました");
					return;
				}
				finally{
					bufferedReader.close();
				}
			}
			//4集計結果出力
			filePrint(args[0],branch[1],branchSales,branchName);
			filePrint(args[0],commodity[1],commoditySales,commodityName);
		}catch(ArrayIndexOutOfBoundsException e){
			System.out.println("売上ファイル名が連番になっていません");
			return;
		}catch(NumberFormatException e){
			System.out.println("売上ファイル名が連番になっていません");
			return;
		}catch(IOException  e){
			System.out.println("予期せぬエラーが発生しました");
			return;
		}

	}


	//支店定義ファイル,商品定義ファイルの読み込み
	public static Map<String,String> fileScan(String place,String lstFile,String category){
		Map<String,String> fileScan = new HashMap<String,String>();
		try{
			File file = new File(place + File.separator + lstFile);
			if(!file.exists()){
				fileScan.put("error",category + "定義ファイルが存在しません");
				return fileScan;
			}
			FileReader fileReader = new FileReader(file);
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			try{
				String line;
				while((line = bufferedReader.readLine()) != null){
					String[] lineDivision = line.split(",");// , で分割
					fileScan.put(lineDivision[0],lineDivision[1]);//キー:コード , 要素:名前
					if(lineDivision.length != 2){					//１行 , が２つじゃない
						fileScan.put("error",category + "定義ファイルのフォーマットが不正です");
						return fileScan;
					}
					if(category == "支店"){
						if (!lineDivision[0].matches("^[0-9]{3}$")) {//数字のみか
							fileScan.put("error",category + "定義ファイルのフォーマットが不正です");
							return fileScan;
						}
					}else if(category == "商品"){
						if(!lineDivision[0].matches("^[0-9a-zA-Z]{8}$")){//英数字か
							fileScan.put("error",category + "定義ファイルのフォーマットが不正です");
							return fileScan;
						}
					}
				}
			}catch(NumberFormatException e){
				fileScan.put("error",category + "定義ファイルのフォーマットが不正です");
				return fileScan;
			}catch(ArrayIndexOutOfBoundsException e){
				fileScan.put("error",category + "定義ファイルのフォーマットが不正です");
				return fileScan;
			}
			finally{
				bufferedReader.close();
			}
		}catch(IOException  e){
			fileScan.put("error","予期せぬエラーが発生しました");
			return fileScan;
		}
		return fileScan;
	}
	//各合計売上の計算
	public static Map<String,Integer> aggregate(String category,int i,String code,String rcdFile,int codeLength,ArrayList<String> valueTemp,Map<String,Integer> salesString,Map<String,String> codePosition){
		Long  aggregate = 0L;
		if(code.length() != codeLength || !codePosition.containsKey(code)) { //１行目にコードがない場合
			salesString.put(rcdFile + "の" + category + "コードが不正です",1000000001);
			return salesString;
		}
		long rcdSales = new Long(valueTemp.get(2));//今回取得した売上
		//売上金額格納	
		aggregate = salesString.get(valueTemp.get(i)) + rcdSales;//支店売上
		if(aggregate > 10000000000L){
			salesString.put("合計金額が10桁を超えました",1000000001);
			return salesString;
		}
		salesString.put(valueTemp.get(i),aggregate.intValue());
		return salesString;
	}
	//支店ごと,商品ごとの売り上げ
	public static void filePrint(String place,String fileName,Map<String,Integer> sales,Map<String,String> namePosition)throws IOException{
		String crlf = System.getProperty("line.separator");
		//並び替え
		List<Map.Entry<String,Integer>> sort = new ArrayList<Map.Entry<String,Integer>>(sales.entrySet());
		Collections.sort(sort, new Comparator<Map.Entry<String,Integer>>() {
			public int compare(
			Entry<String,Integer> sort1, Entry<String,Integer> sort2) {
			return ((Integer)sort2.getValue()).compareTo((Integer)sort1.getValue());
			}
			});
		//ここまで
		File file = new File(place + File.separator + fileName);
		FileWriter fileWriter = new FileWriter(file);
		BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
		for (Entry<String,Integer> i : sort) {
			bufferedWriter.write(i.getKey() + "," + namePosition.get(i.getKey()) + "," + i.getValue() + crlf);
		}
		bufferedWriter.close();
		return;
	}
}

