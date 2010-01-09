package pavBatchUI;

import java.io.*;

import pavBallot.*;
import pav.PAVShared;

public class BatchUI {
	public static void run(){
		voteProcess:
			while (PAVShared.numberOfCastVotes < global.Consts.VOTERS_AMOUNT){
				BufferedReader userIn = new BufferedReader(new InputStreamReader(System.in));  
				System.out.println("Please type in your ID:");

				int voterID = 0;

				try {
					voterID = Integer.parseInt(userIn.readLine());

					if ((voterID < 10000000)||(voterID > 1000000000))
						throw new NumberFormatException();
				} catch (NumberFormatException e) {
					System.err.println("ID error: Please type a valid ID");
					continue;
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					return;
				}

				Ballot b = new Ballot();

				System.out.println("Please pick your vote:\n");

				for (int i = 0; i < global.Consts.PARTIES_AMOUNT;i++){
					Vote v = b.getVote(i);
					String voteBuffer = i+":\t"+v.getCandidateName()+"\n";
					voteBuffer += "Vote Encryption:\n" + v.getEncryptionBase64();
					System.out.println(voteBuffer);
				}

				System.out.println("Options:\n\t0-"+global.Consts.PARTIES_AMOUNT+": Your Selection.\n\tver: Verify Ballot\n\tquit: Finish the voting process");

				String voterIn = "";
				boolean legalVoterIn = false;

				voterInput:
					while (!legalVoterIn){
						try {
							voterIn = userIn.readLine();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

						if (voterIn == "quit") break voteProcess;
						else if (voterIn == "ver"){
							System.out.println("Here is your ballot with corresponding seeds:");

							for (int i = 0; i < global.Consts.PARTIES_AMOUNT;i++){
								Vote v = b.getVote(i);
								String voteBuffer = i+":\t"+v.getCandidateName()+"\n";
								voteBuffer += "Vote Encryption:\n" + v.getEncryptionBase64()+"\n";
								voteBuffer += "Seed used for encryption:\n"+v.getSeedInBase64();
								System.out.println(voteBuffer);
							}

							System.out.println("press any key...");
							try {
								userIn.read();
								continue voteProcess;
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}else{
							int castVote = 0;
							try{
								castVote = Integer.parseInt(voterIn);
								if ((castVote < 0) || (castVote >= global.Consts.PARTIES_AMOUNT)) throw new NumberFormatException();
							} catch (NumberFormatException nfe) {
								System.out.println("Unsupported action, please try again:");
								continue;
							}

							System.out.println("You have chosen to cast the following vote:");

							Vote v = b.getVote(castVote);
							String voteBuffer = castVote+":\t"+v.getCandidateName()+"\n";
							voteBuffer += "Vote Encryption:\n" + v.getEncryptionBase64()+"\n";

							System.out.println(voteBuffer);
							System.out.println("Please confirm [yes|no]:");

							boolean voterLegalConfirm = false;
							while (!voterLegalConfirm){
								String voterConfirm = "";
								try {
									voterConfirm = userIn.readLine();
								} catch (IOException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}

								if (voterConfirm == "yes"){
									legalVoterIn = true;
									voterLegalConfirm = true;
									PAVShared.addCastVote(b.getVote(castVote), voterID+"");
								} else if (voterConfirm == "no"){
									System.out.println("You have cancelled your vote.\nPlease make a selection:");
									continue voterInput;
								} else {
									System.out.println("please select yes or no only.");
								}

							}
						}
					}
			}
	}
}
