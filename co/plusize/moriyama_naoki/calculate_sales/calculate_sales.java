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
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class calculate_sales {
	public static void main(String[] args){
		ArrayList<String> dataList = new ArrayList<String>();
		ArrayList<String> rcdFileList = new ArrayList<String>();
		ArrayList<Integer> checkNum = new ArrayList<Integer>();

		Map<String,Integer> branSales = new HashMap<String,Integer>();//キー:支店コード , 要素:売上金額
		Map<String,String> branName = new HashMap<String,String>();

		Map<String,Integer> comSales = new HashMap<String,Integer>();//キー:商品コード , 要素:売上金額
		Map<String,String> comName = new HashMap<String,String>();
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
			branName = scanFile(args[0],branch[0],"支店",3);		 //1支店定義ファイルの読み込み
			Iterator codeSurch = branName.entrySet().iterator();//支店コードをbranSalesに
			while(codeSurch.hasNext()) {
				Map.Entry getCode = (Map.Entry)codeSurch.next();
				branSales.put((String)getCode.getKey(),0);
			}
			if(branName.containsKey("error")){
				System.out.println(branName.get("error"));
				return;
			}
			comName = scanFile(args[0],commodity[0],"商品",8);		//2商品定義ファイルの読み込み
			codeSurch = comName.entrySet().iterator();
			while(codeSurch.hasNext()) {
				Map.Entry getCode = (Map.Entry)codeSurch.next();
				comSales.put((String)getCode.getKey(),0);
			}
			if(comName.containsKey("error")){
				System.out.println(comName.get("error"));
				return;
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
		//rcdファイル探し終わり
		//ファイル読み込み
			for(int i = 0;i < rcdFileList.size();i ++){
				File surchFile = new File(args[0] + File.separator + rcdFileList.get(i));
				int num = Integer.parseInt(rcdFileList.get(i).substring(0,8)); //rcdファイル名を数字に
				checkNum.add(i);//読み込めたらsizeが一つ増える
				//連番チェック
				if(checkNum.size() != num){
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
						dataList.add(line);
						if(j < 2){
							surch[j] = line;//surch[0]に支店コード,surch[1]に商品コード
						}
						j++;
					}
					//３行じゃない場合
					if(j != 3){
						System.out.println(rcdFileList.get(i) + "のフォーマットが不正です");
						return;
					}
					branSales = Aggregate("支店",0,i,surch[0],rcdFileList,3,dataList,branSales,branName); //支店ごとの売り上げ
					if(branSales.containsKey(rcdFileList.get(i) + "の支店コードが不正です")){
						System.out.println(rcdFileList.get(i) + "の支店コードが不正です");
						return;
					}else if(branSales.containsKey("合計金額が10桁を超えました")){
						System.out.println("合計金額が10桁を超えました");
						return;
					}
					comSales = Aggregate("商品",1,i,surch[1],rcdFileList,8,dataList,comSales,comName); //商品ごとの売り上げ
					if(comSales.containsKey(rcdFileList.get(i) + "の商品コードが不正です")){
						System.out.println(rcdFileList.get(i) + "の商品コードが不正です");
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
			filePrint(args[0],branch[1],branSales,branName);
			filePrint(args[0],commodity[1],comSales,comName);
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
	public static Map<String,String> scanFile(String place,String fileName,String name,int codeSize){
		Map<String,String> scanFile = new HashMap<String,String>();
		try{
			File file = new File(place + File.separator + fileName);
			if(!file.exists()){
				scanFile.put("error",name + "定義ファイルが存在しません");
				return scanFile;
			}
			FileReader fileReader = new FileReader(file);
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			try{
				String line;
				while((line = bufferedReader.readLine()) != null){
					String[] Data = line.split(",");// , で分割
					scanFile.put(Data[0],Data[1]);//キー:商品コード , 要素:商品名
					//１行 , が２つ以上多くある もしくは 商品コードが８桁でない 場合
					if(Data.length != 2){
						scanFile.put("error",name + "定義ファイルのフォーマットが不正です");
						return scanFile;
					}
					if (Data[0].length() != codeSize) {//アルファベット、数字のみか
						scanFile.put("error",name + "定義ファイルのフォーマットが不正です");
						return scanFile;
					}
					if(name == "支店"){
						if (!Data[0].matches("^[0-9]{3}$")) {//数字のみか
							scanFile.put("error",name + "定義ファイルのフォーマットが不正です");
							return scanFile;
						}
					}else if(name == "商品"){
						if(!Data[0].matches("^[0-9a-zA-Z]{8}$")){
							scanFile.put("error",name + "定義ファイルのフォーマットが不正です");
							return scanFile;
						}
					}
				}
			}catch(NumberFormatException e){
				scanFile.put("error",name + "定義ファイルのフォーマットが不正です");
				return scanFile;
			}catch(ArrayIndexOutOfBoundsException e){
				scanFile.put("error",name + "定義ファイルのフォーマットが不正です");
				return scanFile;
			}
			finally{
				bufferedReader.close();
			}
		}catch(IOException  e){
			scanFile.put("error","予期せぬエラーが発生しました");
			return scanFile;
		}
		return scanFile;
	}

	//支店ごと,商品ごとの売り上げ
	public static void filePrint(String place,String fileName,Map<String,Integer> sales,Map<String,String> name)throws IOException{
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
			bufferedWriter.write(i.getKey() + "," + name.get(i.getKey()) + "," + i.getValue() + crlf);
		}
		bufferedWriter.close();
		return;
	}

	//各合計売上の計算
	public static Map<String,Integer> Aggregate(String name,int i,int j,String code,ArrayList<String> surch,int size,ArrayList<String> list,Map<String,Integer> sales,Map<String,String> data){
		long  add = 0;
		if(code.length() != size || !data.containsKey(code)) { //１行目に支店コードがない場合
			sales.put(surch.get(j) + "の" + name + "コードが不正です",1000000001);
			return sales;
		}
		long rcdSales = new Long(list.get(2));//今回取得した売上
		//売上金額格納	
		if(sales.containsKey(code)){
			add = sales.get(list.get(i)) + rcdSales;//支店売上
		}else{
			add = rcdSales;//支店売上
		}
		if(add > 1000000000){
			sales.put("合計金額が10桁を超えました",1000000001);
			return sales;
		}
		sales.put(list.get(i),(int)add);
		return sales;
	}

}

