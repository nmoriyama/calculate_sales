import java.io.*;
import java.util.*;

public class umain {
	public static void main(String[] args){
		ArrayList dataList=new ArrayList();

		try{                                     //1-1支店定義ファイルの読み込み
			File file1=new File(args[0]+"\\branch.lst");
			FileReader fr1=new FileReader(file1);
			BufferedReader br1=new BufferedReader(fr1);
			String s1;
			int x=0;
			while((s1=br1.readLine())!=null){	
				String[] branch=s1.split(",");//奇数:支店コード、偶数:支店名
				x++;
			}
			String branch_code[]=new String[x/2];
			String branch_name[]=new String[x/2];
			for(int i=0;i<x;i++){
				if(i%2==0){
					branch_name[i]=branch[i];
				}else{
					branch_code[i]=branch[i];
				}
			}

			br1.close();
		}catch(IOException e){
			System.out.println("支店定義ファイルのフォーマットが不正です");
		}
		
		try{                                   //1-2商品定義ファイルの読み込み
			File file2=new File(args[0]+"\\commodity.lst");
			FileReader fr2=new FileReader(file2);
			BufferedReader br2=new BufferedReader(fr2);
			String s2=br2.readLine();
			String[] comms=s2.split(",");//奇数:商品コード、偶数:商品名
			
			br2.close();
		}catch(IOException e){
			System.out.println("商品定義ファイルのフォーマットが不正です");
		}
		
		try{              //1-3
			for(int data=0;data<100;data++){
				//このままだと８桁の数字ではないので文字列にする必要
				File file3=new File(args[0]+"\\"+data+".rcd");
				FileReader fr3=new FileReader(file3);
				BufferedReader br3=new BufferedReader(fr3);
				String s3;
				while((s3=br3.readLine())!=null){
					dataList.add(s3);
				}
				
				try{                               //加算していく
					
					
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
			
			File file4=new File(args[0]+"\\branch.out");
			FileWriter fw4=new FileWriter(file4);
			BufferedWriter bw4=new BufferedWriter(fw4);
			bw.write(+","++","+\r\n);
			
			File file5=new File(args[0]+"\\commodity.out");
			FileWriter fw5=new FileWriter(file5);
			BufferedWriter bw5=new BufferedWriter(fw5);
			bw.write(+","++","+\r\n);
		}catch(IOException e){
			System.out.println("予期せぬエラーが発生しました");
		}


	}

}
