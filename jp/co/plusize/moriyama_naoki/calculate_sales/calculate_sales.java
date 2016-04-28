package jp.co.plusize.moriyama_naoki.calculate_sales;

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
		ArrayList<String> rcdFileList = new ArrayList<String>();
		
		HashMap<String,Integer> branSalesMap = new HashMap<String,Integer>();
		HashMap<String,String> branNameMap = new HashMap<String,String>();
		HashMap<Integer,String> branCodeMap = new HashMap<Integer,String>();
		HashMap<String,Integer> branCodeSalesMap = new HashMap<String,Integer>();
		
		HashMap<String,Integer> comSales = new HashMap<String,Integer>();
		HashMap<String,String> comName = new HashMap<String,String>();
		HashMap<Integer,String> comCode = new HashMap<Integer,String>();
		HashMap<String,Integer> comCodeSalesMap = new HashMap<String,Integer>();
		
		TreeMap<Integer,String> BranchMap = new TreeMap<Integer,String>();
		TreeMap<Integer,String> CommodityMap = new TreeMap<Integer,String>();
		//int rcdNumber = 0,count = 0;
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
			File branchFile;
			if(args[0].endsWith(File.separator)){
				branchFile = new File(args[0] +"branch.lst");
			}else{
				branchFile = new File(args[0] + File.separator +"branch.lst");
			}
			if(!branchFile.exists()){
				System.out.println("支店定義ファイルが存在しません");
				return;
			}
			
			FileReader fileReader = new FileReader(branchFile);
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			
			try{
				String line;
				int count = 0;
				while((line = bufferedReader.readLine()) != null){
					String[] branch = line.split(",");// , で分割
					/*if(branch[1].indexOf("支店") != 2){
						System.out.println("支店定義ファイルのフォーマットが不正です");
						return;
					}*/
					branSalesMap.put(branch[0],0);//キー:支店コード , 要素:売上金額
					branNameMap.put(branch[0],branch[1]);//キー:支店コード , 要素:支店名
					branCodeMap.put(count,branch[0]);//キー:０～ , 要素：支店コード
					//１行 , が２つ以上多くある もしくは 支店コードが３桁でない 場合
					if(branch.length != 2 || branch[0].length() != 3){ 
						System.out.println("支店定義ファイルのフォーマットが不正です");
						return;
					}
					if (!branch[0].matches("^[0-9]+$")) {//数字のみか
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
			File comFile;
			if(args[0].endsWith(File.separator)){
				comFile = new File(args[0] + "commodity.lst");
			}else{
				comFile = new File(args[0] + File.separator + "commodity.lst");
			}
			if(!comFile.exists()){
				System.out.println("商品定義ファイルが存在しません");
				return;
			}
			
			FileReader comFR = new FileReader(comFile);
			BufferedReader comBR = new BufferedReader(comFR);
			
			try{
				String line;
				int count = 0;
				while((line = comBR.readLine()) != null){
					String[] commodity = line.split(",");// , で分割
					comSales.put(commodity[0],0);//キー:商品コード , 要素:売上金額
					comName.put(commodity[0],commodity[1]);//キー:商品コード , 要素:商品名
					comCode.put(count, commodity[0]);//キー:０～ , 要素：商品コード
					//１行 , が２つ以上多くある もしくは 商品コードが８桁でない 場合
					if(commodity.length != 2 || commodity[0].length() != 8){
						System.out.println("商品定義ファイルのフォーマットが不正です");
						return;
					}
					if (!commodity[0].matches("^[0-9a-zA-Z]+$")) {//アルファベット、数字のみか
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
				comBR.close();
			}
		}catch(IOException  e){
			System.out.println("予期せぬエラーが発生しました");
			System.out.println(e);
			return;
		}

		
		//3集計
		//売上ファイルのある場所の検索
		File rcdFile;
		if(args[0].endsWith(File.separator)){
			rcdFile = new File(args[0]);
		}else{
			rcdFile = new File(args[0]);
		}
		String[] rcdFileString = rcdFile.list();
		for(int i = 0;i <rcdFileString.length;i ++){
			String line = rcdFileString[i];
			
			//
			//File directory = new File(args[0]);
			File[] filelist = rcdFile.listFiles();

			if (filelist[i].isFile()){
				// ファイルだった時の処理
			}else if (filelist[i].isDirectory()){
				String surchNumber = String.valueOf(i+2);
				String surch = "0";
				while(surch.length() < (8 - surchNumber.length())){
					surch += "0";
				}
				surch = surch.concat(surchNumber);
				File surchFile;
				if(args[0].endsWith(File.separator)){
					surchFile = new File(args[0] + surch + ".rcd");
				}else{
					surchFile = new File(args[0] + File.separator + surch + ".rcd");
				}	
				//System.out.println(i+2+" , "+filelist[i+2]);
				if(!surchFile.exists()/*isFile().equals(surchFile)*/){
					rcdSize = 1;
					//System.out.println(rcdSize+" , "+rcdFileList.size());
					i++;
					//見つからなかったら
				}else{
					//見つかったら
					//System.out.println(rcdSize+" + "+rcdFileList.size());
					System.out.println("売上ファイル名が連番になっていません");
					return;
			
				}
			}
			//拡張子がrcd かつ 12桁(８桁+拡張子(.rcd))
			if(line.endsWith("rcd") && line.length() == 12){
				Integer.parseInt(line.substring(0,8));//rcdファイルが数字かどうか
				rcdFileList.add(line);
			}else if(line.endsWith("lst") || line.endsWith("out") || line.endsWith("java")){

			}else{				
				System.out.println("売上ファイル名が連番になっていません");
				return;
			}
		}//rcdファイル探し終わり

		
		//ファイル読み込み
		try{
			int rcdNumber = 1;
			//System.out.println(rcdSize);
			for(int i = 0;i < rcdFileList.size()-rcdSize;i ++){
				String RcdNumber = String.valueOf(rcdNumber);
				String comString = "0";
				while(comString.length() < (8 - RcdNumber.length())){
					comString += "0";
				}
				comString = comString.concat(RcdNumber);//comStringにファイル名
				File rcdSalesFile;
				if(args[0].endsWith(File.separator)){
					rcdSalesFile = new File(args[0] + comString + ".rcd");
				}else{
					rcdSalesFile = new File(args[0] + File.separator + comString + ".rcd");
				}
				
				

				//連番チェック
				if(!rcdSalesFile.exists()){//読み込めなかったら
					RcdNumber = String.valueOf(rcdNumber+1);
					comString = "0";
					while(comString.length() < (8 - RcdNumber.length())){
						comString += "0";
					}
					comString = comString.concat(RcdNumber);
					if(args[0].endsWith(File.separator)){
						rcdSalesFile = new File(args[0] + comString + ".rcd");
					}else{
						rcdSalesFile = new File(args[0] + File.separator + comString + ".rcd");
					}
					
					if(!rcdSalesFile.exists()){//読み込めなかった場合
						System.out.println("予期せぬエラーが発生しました");
						return;

					}else{//連番じゃなかった場合
						System.out.println("売上ファイル名が連番になっていません");
						return;
					}
				}	
				
				FileReader rcdSalesFileFR = new FileReader(rcdSalesFile);
				BufferedReader rcdSalesFileBR = new BufferedReader(rcdSalesFileFR);
				
				try{
					long branchSales = 0,comoditySales = 0;
					String line;
					
					int k=0;
					while((line = rcdSalesFileBR.readLine()) != null){
						dataList.add(line);
						if(k == 0){ //１行目に支店コードがない場合
							if(!branSalesMap.containsKey(line)){
								System.out.println(comString + ".rcdの支店コードが不正です");
								return;
							}
						}else if(k == 1){//２行目に商品コードがない場合
							if(!comSales.containsKey(line)){
								System.out.println(comString + ".rcdの商品コードが不正です");	
								return;
							}
						}
						k++;
					}

					if(k != 3){
						System.out.println(comString + ".rcdのフォーマットが不正です");	
						return;
					}
				
					int rcdSales = Integer.parseInt(dataList.get(2));//今回取得した売上
					branchSales = branSalesMap.get(dataList.get(0)) + rcdSales;//支店売上
					comoditySales = comSales.get(dataList.get(1)) + rcdSales;//商品売上
				
					if(branchSales >= 1000000000 || comoditySales >= 1000000000){
						System.out.println("合計金額が10桁を超えました");
						return;
					}
					
					branSalesMap.put(dataList.get(0),(int)branchSales);
					branCodeSalesMap.put(dataList.get(0),(int)branchSales);//支店コードと売上
 
					comSales.put(dataList.get(1),(int)comoditySales);
					comCodeSalesMap.put(dataList.get(1),(int)comoditySales);//商品コードと売上
					rcdNumber ++;
					dataList.clear();
				}catch(IOException e){
					System.out.println("予期せぬエラーが発生しました");
					return;
				}
				finally{
					rcdSalesFileBR.close();				
				}
			}
			
		}catch(IOException  e){
			System.out.println("予期せぬエラーが発生しました");
			return;
		}
	
		//4集計結果出力		
		try{
			File branOutFile;
			if(args[0].endsWith(File.separator)){
				branOutFile = new File(args[0] + "branch.out");
			}else{
				branOutFile = new File(args[0] + File.separator + "branch.out");
			}
			FileWriter branOutFileFW = new FileWriter(branOutFile);
			BufferedWriter branOutFileBW = new BufferedWriter(branOutFileFW);	
			
			try{
				//int rcdNumber = 1;
				int count = 0;
				for(int i = 0;i < branSalesMap.size();i++){

					if(branCodeSalesMap.get(branCodeMap.get(count)) != null){
						BranchMap.put(branCodeSalesMap.get(branCodeMap.get(count)),branCodeMap.get(count));//treemap にキー:売上金額 要素:支店コード
					}
					count++;
					//rcdNumber++;
				}
			//書き込み
				branOutFileBW.write(BranchMap.get(BranchMap.lastKey()) + "," + branNameMap.get(BranchMap.get(BranchMap.lastKey())) + "," + BranchMap.lastKey() + "\r\n");
				int branOutWrite = BranchMap.lastKey();
				while(BranchMap.lowerKey(branOutWrite) != null){
					branOutFileBW.write(BranchMap.get(BranchMap.lowerKey(branOutWrite)) + "," + branNameMap.get(BranchMap.get(BranchMap.lowerKey(branOutWrite))) + "," + BranchMap.lowerKey(branOutWrite) + "\r\n");
					branOutWrite = BranchMap.lowerKey(branOutWrite);
				}
			}catch(IOException  e){
				branOutFile.delete();
				System.out.println("予期せぬエラーが発生しました");
				return;
			}
			finally{
				branOutFileBW.close();
			}
		}catch(IOException  e){
			System.out.println("予期せぬエラーが発生しました");
			return;
		}
	
		try{		
			File comout;
			if(args[0].endsWith(File.separator)){
				comout = new File(args[0] + "commodity.out");
			}else{
				comout = new File(args[0] + File.separator + "commodity.out");
			}
			FileWriter comoutFW = new FileWriter(comout);
			BufferedWriter comoutBW = new BufferedWriter(comoutFW);
			try{

				//int rcdNumber = 1;
				int count = 0;
				for(int i = 0;i < comSales.size();i ++){

					if(comCodeSalesMap.get(comCode.get(count)) != null){
						CommodityMap.put(comCodeSalesMap.get(comCode.get(count)),comCode.get(count));//treemap にキー:売上金額 要素:商品コード
					}
					count ++;
					//rcdNumber ++;
				}
			//書き込み
				comoutBW.write(CommodityMap.get(CommodityMap.lastKey()) + "," + comName.get(CommodityMap.get(CommodityMap.lastKey())) + "," + CommodityMap.lastKey() + "\r\n");
				int comOutWrite = CommodityMap.lastKey();
				while(CommodityMap.lowerKey(comOutWrite) != null){
					comoutBW.write(CommodityMap.get(CommodityMap.lowerKey(comOutWrite)) + "," + comName.get(CommodityMap.get(CommodityMap.lowerKey(comOutWrite))) + "," + CommodityMap.lowerKey(comOutWrite) + "\r\n");
					comOutWrite = CommodityMap.lowerKey(comOutWrite);
				}
			
			}catch(IOException e){			
				comout.delete();
				System.out.println("予期せぬエラーが発生しました");
				return;
			}	
			finally{
				comoutBW.close();
			}
	
		}catch(IOException  e){
			System.out.println("予期せぬエラーが発生しました");
			return;
		}
	}
}
