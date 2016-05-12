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
	public static void main(String[] args) {
		ArrayList<String> rcdFileList = new ArrayList<String>();
		ArrayList<Integer> sequenceCheck = new ArrayList<Integer>();

		Map<String, Long> branchSales = new HashMap<String, Long>();//キー:支店コード , 要素:売上金額
		Map<String, String> branchName = new HashMap<String, String>();
		Map<String, Long> commoditySales = new HashMap<String, Long>();//キー:商品コード , 要素:売上金額
		Map<String, String> commodityName = new HashMap<String, String>();

		String[] branch = {"branch.lst", "branch.out", "支店", "0", null};
		String[] commodity = {"commodity.lst", "commodity.out", "商品", "1", null};
		int[] branchNumber = {0, 3};
		int[] commodityNumber = {1, 8};
		
		try {
			//コマンドライン引数1つじゃない場合
			if (args.length != 1) {
				System.out.println("予期せぬエラーが発生しました");
				throw new IOException("予期せぬエラーが発生しました");
			}
			if (args[0].endsWith(File.pathSeparator)) {
				args[0] = args[0].substring(0, args[0].length() - 1);
			}
			//定義ファイルの読み込み
			branchName = fileScan(args[0], branch);		 //1支店定義ファイルの読み込み//支店コードをbranSalesに
			if (branchName.containsKey("error")) {
				System.out.println(branchName.get("error"));
				throw new IOException(branchName.get("error"));
			}
			for (String getCode : branchName.keySet()) {
				branchSales.put(getCode,0L);
			}
			commodityName = fileScan(args[0], commodity);		//2商品定義ファイルの読み込み
			if (commodityName.containsKey("error")) {
				System.out.println(commodityName.get("error"));
				throw new IOException(commodityName.get("error"));
			}
			for (String getCode : commodityName.keySet()) {
				commoditySales.put(getCode,0L);
			}
		} catch(IOException e) {
			return;
		}
		//3集計
		//売上ファイルのある場所の検索
		try	{
			File file = new File(args[0]);
			String[] allFile = file.list();//探す場所にあるファイル全部
			File[] checkDirectory = file.listFiles();
			for (int i = 0; i < allFile.length; i ++) {
				if (! checkDirectory[i].isDirectory() && allFile[i].matches("^[0-9]{8}.rcd$")) {	//8桁の数字.rcdか
					rcdFileList.add(allFile[i]);
				}
			}
			//ファイル読み込み
			for (int i = 0; i < rcdFileList.size(); i ++) {
				File surchFile = new File(args[0] + File.separator + rcdFileList.get(i));
				sequenceCheck.add(i);//読み込めたらsizeが一つ増える
				//連番チェック
				if (sequenceCheck.size() != Integer.parseInt(rcdFileList.get(i).substring(0, 8))) {
					System.out.println("売上ファイル名が連番になっていません");//読み込めなかったら
					return;
				}
				FileReader fileReader = new FileReader(surchFile);
				BufferedReader bufferedReader = new BufferedReader(fileReader);
				try {
					String line;
					ArrayList<String> rcdLoaded = new ArrayList<String>();
					while ((line = bufferedReader.readLine()) != null) {
						rcdLoaded.add(line);
					}
					if (rcdLoaded.size() != 3) {//３行じゃない場合
						System.out.println(rcdFileList.get(i) + "のフォーマットが不正です");
						return;
					}
					boolean errorCheck = (rcdLoaded.get(branchNumber[0]).length() != branchNumber[1] || ! branchName.containsKey(rcdLoaded.get(branchNumber[0])));
					branchSales = aggregate(rcdLoaded.get(Integer.parseInt(branch[3])), Long.parseLong(rcdLoaded.get(2)), branchSales, errorCheck, rcdFileList.get(i), branch[2]); //支店ごとの売り上げ
					if (branchSales.containsKey("error")) {
						return;
					}
					errorCheck = (rcdLoaded.get(commodityNumber[0]).length() != commodityNumber[1] || ! commodityName.containsKey(rcdLoaded.get(commodityNumber[0])));
					commoditySales = aggregate(rcdLoaded.get(Integer.parseInt(commodity[3])), Long.parseLong(rcdLoaded.get(2)), commoditySales, errorCheck, rcdFileList.get(i), commodity[2]); //商品ごとの売り上げ
					if (commoditySales.containsKey("error")) {
						return;
					}
				} catch(IOException e) {
					System.out.println("予期せぬエラーが発生しました");
					return;
				}
				finally {
					bufferedReader.close();
				}
			}
			//4集計結果出力
			filePrint(args[0], branch[1], branchSales, branchName);
			filePrint(args[0], commodity[1], commoditySales, commodityName);
		} catch (ArrayIndexOutOfBoundsException e) {
			System.out.println("売上ファイル名が連番になっていません");
			return;
		} catch (NumberFormatException e) {
			System.out.println("売上ファイル名が連番になっていません");
			return;
		} catch (IOException  e) {
			System.out.println("予期せぬエラーが発生しました");
			return;
		}
	}


	//支店定義ファイル,商品定義ファイルの読み込み
	public static Map<String, String> fileScan(String place, String[] lstFile) {
		Map<String, String> fileScan = new HashMap<String, String>();
		try {
			File file = new File(place + File.separator + lstFile[0]);
			if (! file.exists()) {
				fileScan.put("error", lstFile[2] + "定義ファイルが存在しません");
				return fileScan;
			}
			FileReader fileReader = new FileReader(file);
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			try {
				String line;
				while ((line = bufferedReader.readLine()) != null) {
					String[] lineDivision = line.split(",");// , で分割
					fileScan.put(lineDivision[0], lineDivision[1]);//キー:コード , 要素:名前
					if (lineDivision.length != 2) {					//１行 に, が２つじゃない
						fileScan.put("error", lstFile[2] + "定義ファイルのフォーマットが不正です");
						return fileScan;
					}
					if (lstFile[2] == "支店" && ! lineDivision[0].matches("^[0-9]{3}$")) {//数字のみか
						fileScan.put("error", lstFile[2] + "定義ファイルのフォーマットが不正です");
						return fileScan;
					}
					if (lstFile[2] == "商品" && ! lineDivision[0].matches("^[0-9a-zA-Z]{8}$")) {//英数字か
						fileScan.put("error", lstFile[2] + "定義ファイルのフォーマットが不正です");
						return fileScan;
					}
				}
			} catch(NumberFormatException e) {
				fileScan.put("error", lstFile[2] + "定義ファイルのフォーマットが不正です");
				return fileScan;
			} catch(ArrayIndexOutOfBoundsException e) {
				fileScan.put("error", lstFile[2] + "定義ファイルのフォーマットが不正です");
				return fileScan;
			}
			finally {
				bufferedReader.close();
			}
		} catch(IOException e) {
			fileScan.put("error", "予期せぬエラーが発生しました");
			return fileScan;
		}
		return fileScan;
	}
	//各合計売上の計算
	public static Map<String, Long> aggregate(String code, Long rcdSales, Map<String,Long> categorySales, boolean errorCheck, String rcdFile, String category) {
		if(errorCheck){
			categorySales.put("error", 10000000001L);
			System.out.println(rcdFile + "の" + category + "コードが不正です");
			return categorySales;
		}
		Long aggregate = categorySales.get(code) + rcdSales;
		if (aggregate >= 10000000000L) {//１０桁を超えた場合
			categorySales.put("error", 10000000001L);
			System.out.println("合計金額が10桁を超えました");
			return categorySales;
		}
		categorySales.put(code, aggregate);
		return categorySales;
	}
	//支店ごと,商品ごとの売り上げ
	public static void filePrint(String place, String fileName, Map<String,Long> sales, Map<String,String> namePosition) throws IOException {
		String crlf = System.getProperty("line.separator");
		//並び替え
		List<Entry<String,Long>> sort = new ArrayList<Entry<String,Long>>(sales.entrySet());
		Collections.sort(sort, new Comparator<Entry<String, Long>>() {
			public int compare(Entry<String, Long> sort1, Entry<String, Long> sort2) {
				return (sort2.getValue()).compareTo(sort1.getValue());
			}
		} );
		//ここまで
		File file = new File(place + File.separator + fileName);
		FileWriter fileWriter = new FileWriter(file);
		BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
		for (Entry<String,Long> i : sort) {
			bufferedWriter.write(i.getKey() + "," + namePosition.get(i.getKey()) + "," + i.getValue() + crlf);
		}
		bufferedWriter.close();
		return;
	}
}