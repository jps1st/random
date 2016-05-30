package pint;

// $ANTLR 3.4 C:\\Users\\admin\\Documents\\randompii\\Pint.g 2016-03-15 14:12:43

import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;

import org.antlr.runtime.tree.*;


@SuppressWarnings({"all", "warnings", "unchecked"})
public class PintParser extends Parser {
    public static final String[] tokenNames = new String[] {
        "<invalid>", "<EOR>", "<DOWN>", "<UP>", "CHAR", "D4", "DIGIT", "DRAW", "END", "INT", "L2", "L3", "P3", "S2", "S3", "S3R", "SEP", "TX", "WORD"
    };

    public static final int EOF=-1;
    public static final int CHAR=4;
    public static final int D4=5;
    public static final int DIGIT=6;
    public static final int DRAW=7;
    public static final int END=8;
    public static final int INT=9;
    public static final int L2=10;
    public static final int L3=11;
    public static final int P3=12;
    public static final int S2=13;
    public static final int S3=14;
    public static final int S3R=15;
    public static final int SEP=16;
    public static final int TX=17;
    public static final int WORD=18;

    // delegates
    public Parser[] getDelegates() {
        return new Parser[] {};
    }

    // delegators


    public PintParser(TokenStream input) {
        this(input, new RecognizerSharedState());
    }
    public PintParser(TokenStream input, RecognizerSharedState state) {
        super(input, state);
    }

protected TreeAdaptor adaptor = new CommonTreeAdaptor();

public void setTreeAdaptor(TreeAdaptor adaptor) {
    this.adaptor = adaptor;
}
public TreeAdaptor getTreeAdaptor() {
    return adaptor;
}
    public String[] getTokenNames() { return PintParser.tokenNames; }
    public String getGrammarFileName() { return "C:\\Users\\admin\\Documents\\randompii\\Pint.g"; }


    public static class sub_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "sub"
    // C:\\Users\\admin\\Documents\\randompii\\Pint.g:22:1: sub : DRAW ^ id body ender ;
    public final PintParser.sub_return sub() throws RecognitionException {
        PintParser.sub_return retval = new PintParser.sub_return();
        retval.start = input.LT(1);


        CommonTree root_0 = null;

        Token DRAW1=null;
        PintParser.id_return id2 =null;

        PintParser.body_return body3 =null;

        PintParser.ender_return ender4 =null;


        CommonTree DRAW1_tree=null;

        try {
            // C:\\Users\\admin\\Documents\\randompii\\Pint.g:22:4: ( DRAW ^ id body ender )
            // C:\\Users\\admin\\Documents\\randompii\\Pint.g:22:6: DRAW ^ id body ender
            {
            root_0 = (CommonTree)adaptor.nil();


            DRAW1=(Token)match(input,DRAW,FOLLOW_DRAW_in_sub135); 
            DRAW1_tree = 
            (CommonTree)adaptor.create(DRAW1)
            ;
            root_0 = (CommonTree)adaptor.becomeRoot(DRAW1_tree, root_0);


            pushFollow(FOLLOW_id_in_sub138);
            id2=id();

            state._fsp--;

            adaptor.addChild(root_0, id2.getTree());

            pushFollow(FOLLOW_body_in_sub140);
            body3=body();

            state._fsp--;

            adaptor.addChild(root_0, body3.getTree());

            pushFollow(FOLLOW_ender_in_sub142);
            ender4=ender();

            state._fsp--;

            adaptor.addChild(root_0, ender4.getTree());

            }

            retval.stop = input.LT(-1);


            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "sub"


    public static class id_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "id"
    // C:\\Users\\admin\\Documents\\randompii\\Pint.g:24:1: id : TX ^ INT ;
    public final PintParser.id_return id() throws RecognitionException {
        PintParser.id_return retval = new PintParser.id_return();
        retval.start = input.LT(1);


        CommonTree root_0 = null;

        Token TX5=null;
        Token INT6=null;

        CommonTree TX5_tree=null;
        CommonTree INT6_tree=null;

        try {
            // C:\\Users\\admin\\Documents\\randompii\\Pint.g:24:3: ( TX ^ INT )
            // C:\\Users\\admin\\Documents\\randompii\\Pint.g:24:5: TX ^ INT
            {
            root_0 = (CommonTree)adaptor.nil();


            TX5=(Token)match(input,TX,FOLLOW_TX_in_id149); 
            TX5_tree = 
            (CommonTree)adaptor.create(TX5)
            ;
            root_0 = (CommonTree)adaptor.becomeRoot(TX5_tree, root_0);


            INT6=(Token)match(input,INT,FOLLOW_INT_in_id152); 
            INT6_tree = 
            (CommonTree)adaptor.create(INT6)
            ;
            adaptor.addChild(root_0, INT6_tree);


            }

            retval.stop = input.LT(-1);


            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "id"


    public static class body_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "body"
    // C:\\Users\\admin\\Documents\\randompii\\Pint.g:26:1: body : ( bet )+ ;
    public final PintParser.body_return body() throws RecognitionException {
        PintParser.body_return retval = new PintParser.body_return();
        retval.start = input.LT(1);


        CommonTree root_0 = null;

        PintParser.bet_return bet7 =null;



        try {
            // C:\\Users\\admin\\Documents\\randompii\\Pint.g:26:5: ( ( bet )+ )
            // C:\\Users\\admin\\Documents\\randompii\\Pint.g:26:7: ( bet )+
            {
            root_0 = (CommonTree)adaptor.nil();


            // C:\\Users\\admin\\Documents\\randompii\\Pint.g:26:7: ( bet )+
            int cnt1=0;
            loop1:
            do {
                int alt1=2;
                int LA1_0 = input.LA(1);

                if ( (LA1_0==D4||(LA1_0 >= L2 && LA1_0 <= S3R)) ) {
                    alt1=1;
                }


                switch (alt1) {
            	case 1 :
            	    // C:\\Users\\admin\\Documents\\randompii\\Pint.g:26:7: bet
            	    {
            	    pushFollow(FOLLOW_bet_in_body159);
            	    bet7=bet();

            	    state._fsp--;

            	    adaptor.addChild(root_0, bet7.getTree());

            	    }
            	    break;

            	default :
            	    if ( cnt1 >= 1 ) break loop1;
                        EarlyExitException eee =
                            new EarlyExitException(1, input);
                        throw eee;
                }
                cnt1++;
            } while (true);


            }

            retval.stop = input.LT(-1);


            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "body"


    public static class bet_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "bet"
    // C:\\Users\\admin\\Documents\\randompii\\Pint.g:28:1: bet : beth ^ ( INT )* ;
    public final PintParser.bet_return bet() throws RecognitionException {
        PintParser.bet_return retval = new PintParser.bet_return();
        retval.start = input.LT(1);


        CommonTree root_0 = null;

        Token INT9=null;
        PintParser.beth_return beth8 =null;


        CommonTree INT9_tree=null;

        try {
            // C:\\Users\\admin\\Documents\\randompii\\Pint.g:28:4: ( beth ^ ( INT )* )
            // C:\\Users\\admin\\Documents\\randompii\\Pint.g:28:6: beth ^ ( INT )*
            {
            root_0 = (CommonTree)adaptor.nil();


            pushFollow(FOLLOW_beth_in_bet167);
            beth8=beth();

            state._fsp--;

            root_0 = (CommonTree)adaptor.becomeRoot(beth8.getTree(), root_0);

            // C:\\Users\\admin\\Documents\\randompii\\Pint.g:28:12: ( INT )*
            loop2:
            do {
                int alt2=2;
                int LA2_0 = input.LA(1);

                if ( (LA2_0==INT) ) {
                    alt2=1;
                }


                switch (alt2) {
            	case 1 :
            	    // C:\\Users\\admin\\Documents\\randompii\\Pint.g:28:13: INT
            	    {
            	    INT9=(Token)match(input,INT,FOLLOW_INT_in_bet171); 
            	    INT9_tree = 
            	    (CommonTree)adaptor.create(INT9)
            	    ;
            	    adaptor.addChild(root_0, INT9_tree);


            	    }
            	    break;

            	default :
            	    break loop2;
                }
            } while (true);


            }

            retval.stop = input.LT(-1);


            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "bet"


    public static class beth_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "beth"
    // C:\\Users\\admin\\Documents\\randompii\\Pint.g:30:1: beth : ( L2 | L3 | S3 | S2 | S3R | P3 | D4 );
    public final PintParser.beth_return beth() throws RecognitionException {
        PintParser.beth_return retval = new PintParser.beth_return();
        retval.start = input.LT(1);


        CommonTree root_0 = null;

        Token set10=null;

        CommonTree set10_tree=null;

        try {
            // C:\\Users\\admin\\Documents\\randompii\\Pint.g:30:6: ( L2 | L3 | S3 | S2 | S3R | P3 | D4 )
            // C:\\Users\\admin\\Documents\\randompii\\Pint.g:
            {
            root_0 = (CommonTree)adaptor.nil();


            set10=(Token)input.LT(1);

            if ( input.LA(1)==D4||(input.LA(1) >= L2 && input.LA(1) <= S3R) ) {
                input.consume();
                adaptor.addChild(root_0, 
                (CommonTree)adaptor.create(set10)
                );
                state.errorRecovery=false;
            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                throw mse;
            }


            }

            retval.stop = input.LT(-1);


            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "beth"


    public static class ender_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "ender"
    // C:\\Users\\admin\\Documents\\randompii\\Pint.g:32:1: ender : END ^ ( INT )? ;
    public final PintParser.ender_return ender() throws RecognitionException {
        PintParser.ender_return retval = new PintParser.ender_return();
        retval.start = input.LT(1);


        CommonTree root_0 = null;

        Token END11=null;
        Token INT12=null;

        CommonTree END11_tree=null;
        CommonTree INT12_tree=null;

        try {
            // C:\\Users\\admin\\Documents\\randompii\\Pint.g:32:6: ( END ^ ( INT )? )
            // C:\\Users\\admin\\Documents\\randompii\\Pint.g:32:8: END ^ ( INT )?
            {
            root_0 = (CommonTree)adaptor.nil();


            END11=(Token)match(input,END,FOLLOW_END_in_ender212); 
            END11_tree = 
            (CommonTree)adaptor.create(END11)
            ;
            root_0 = (CommonTree)adaptor.becomeRoot(END11_tree, root_0);


            // C:\\Users\\admin\\Documents\\randompii\\Pint.g:32:13: ( INT )?
            int alt3=2;
            int LA3_0 = input.LA(1);

            if ( (LA3_0==INT) ) {
                alt3=1;
            }
            switch (alt3) {
                case 1 :
                    // C:\\Users\\admin\\Documents\\randompii\\Pint.g:32:13: INT
                    {
                    INT12=(Token)match(input,INT,FOLLOW_INT_in_ender215); 
                    INT12_tree = 
                    (CommonTree)adaptor.create(INT12)
                    ;
                    adaptor.addChild(root_0, INT12_tree);


                    }
                    break;

            }


            }

            retval.stop = input.LT(-1);


            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "ender"

    // Delegated rules


 

    public static final BitSet FOLLOW_DRAW_in_sub135 = new BitSet(new long[]{0x0000000000020000L});
    public static final BitSet FOLLOW_id_in_sub138 = new BitSet(new long[]{0x000000000000FC20L});
    public static final BitSet FOLLOW_body_in_sub140 = new BitSet(new long[]{0x0000000000000100L});
    public static final BitSet FOLLOW_ender_in_sub142 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_TX_in_id149 = new BitSet(new long[]{0x0000000000000200L});
    public static final BitSet FOLLOW_INT_in_id152 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_bet_in_body159 = new BitSet(new long[]{0x000000000000FC22L});
    public static final BitSet FOLLOW_beth_in_bet167 = new BitSet(new long[]{0x0000000000000202L});
    public static final BitSet FOLLOW_INT_in_bet171 = new BitSet(new long[]{0x0000000000000202L});
    public static final BitSet FOLLOW_END_in_ender212 = new BitSet(new long[]{0x0000000000000202L});
    public static final BitSet FOLLOW_INT_in_ender215 = new BitSet(new long[]{0x0000000000000002L});

}