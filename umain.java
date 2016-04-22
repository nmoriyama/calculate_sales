import java.io.*;
import java.util.*;

public class umain {
	public static void main(String[] args){
		ArrayList<Integer> dataList = new ArrayList<Integer>();
		ArrayList<Integer> branchList1 = new ArrayList<Integer>();
		ArrayList branchList2 = new ArrayList();
		ArrayList<Integer> commodityList1 = new ArraryList<Integer>();
		ArrayList commodityList2 = new ArraryList();
		int x = 0;
		try{                                     //1-1支店定義ファイルの読み込み
			File file1 = new File(args[0]+"\\branch.lst");
			FileReader fr1 = new FileReader(file1);
			BufferedReader br1 = new BufferedReader(fr1);
			String s1;
			x = 0;
			while((s1 = br1.readLine()) != null){	
				String[] branch = s1.split(",");//奇数:支店コード、偶数:支店名
				x++;
				if(x%2 == 0){
					int data1 = Integer.parseInt(branch[x]);
					branchList1.add(data1);
				}else{
					branchList2.add(branch[x]);
				}
			}
			br1.close();
		}catch(IOException e){
			System.out.println("支店定義ファイルのフォーマットが不正です");
		}
		
		try{                                   //1-2商品定義ファイルの読み込み
			File file2 = new File(args[0]+"\\commodity.lst");
			FileReader fr2 = new FileReader(file2);
			BufferedReader br2 = new BufferedReader(fr2);
			String s2;
			x = 0;
			while((s2 = br2.readLine()) != null){	
				String[] commodity = s2.split(",");//奇数:支店コード、偶数:支店名
				x++;
				if(x%2 == 0){
					int data2 = Integer.parseInt(comodity[x]);
					commodityList1.add(data2);
				}else{
					commodityList2.add(comodity[x]);
				}
			}
			br2.close();
		}catch(IOException e){
			System.out.println("商品定義ファイルのフォーマットが不正です");
		}
		
		try{              //1-3
			for(int data = 0;data<100;data++){
				//このままだと８桁の数字ではないので文字列にする必要
				File file3 = new File(args[0]+"\\"+data+".rcd");
				FileReader fr3 = new FileReader(file3);
				BufferedReader br3 = new BufferedReader(fr3);
				String s3;
				while((s3 = br3.readLine())! = null){
					dataList.add(s3);
				}
				
				try{                               //加算していく
					File file3 = new File(args[0]+"\\"+data+".rcd");
					
					try{                        //支店コードが見つからなかったとき
						
					}catch(IOException e){
						System.out.println(+"の支店コードが不正です");
					}

					try{                        //商品コードが見つからなかったとき
						
					}catch(IOException e){
						System.out.println(+"の商品コードが不正です");
					}
					try{                        //４行以上あるとき
					}catch(IOException e){
						System.out.println(+"のフォーマットが不正です");
					}
				}catch(IOException e){
					System.out.println("合計金額が10桁を超えました");
				}
			}
		}catch(IOException e){
			System.out.println("売上ファイル名が連番になっていません");
		}


		try{
			//降順に
			
			File file4 = new File(args[0]+"\\branch.out");
			FileWriter fw4 = new FileWriter(file4);
			BufferedWriter bw4 = new BufferedWriter(fw4);
			bw.write(+","++","+\r\n);
			
			File file5 = new File(args[0]+"\\commodity.out");
			FileWriter fw5 = new FileWriter(file5);
			BufferedWriter bw5 = new BufferedWriter(fw5);
			bw.write(+","++","+\r\n);
		}catch(IOException e){
			System.out.println("予期せぬエラーが発生しました");
		}


	}

}
