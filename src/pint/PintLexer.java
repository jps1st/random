package pint;

// $ANTLR 3.4 C:\\Users\\admin\\Documents\\randompii\\Pint.g 2016-03-15 14:12:43

import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;

@SuppressWarnings({"all", "warnings", "unchecked"})
public class PintLexer extends Lexer {
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
    // delegators
    public Lexer[] getDelegates() {
        return new Lexer[] {};
    }

    public PintLexer() {} 
    public PintLexer(CharStream input) {
        this(input, new RecognizerSharedState());
    }
    public PintLexer(CharStream input, RecognizerSharedState state) {
        super(input,state);
    }
    public String getGrammarFileName() { return "C:\\Users\\admin\\Documents\\randompii\\Pint.g"; }

    // $ANTLR start "D4"
    public final void mD4() throws RecognitionException {
        try {
            int _type = D4;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // C:\\Users\\admin\\Documents\\randompii\\Pint.g:2:4: ( '4d' )
            // C:\\Users\\admin\\Documents\\randompii\\Pint.g:2:6: '4d'
            {
            match("4d"); 



            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "D4"

    // $ANTLR start "END"
    public final void mEND() throws RecognitionException {
        try {
            int _type = END;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // C:\\Users\\admin\\Documents\\randompii\\Pint.g:3:5: ( 'e' )
            // C:\\Users\\admin\\Documents\\randompii\\Pint.g:3:7: 'e'
            {
            match('e'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "END"

    // $ANTLR start "L2"
    public final void mL2() throws RecognitionException {
        try {
            int _type = L2;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // C:\\Users\\admin\\Documents\\randompii\\Pint.g:4:4: ( 'l2' )
            // C:\\Users\\admin\\Documents\\randompii\\Pint.g:4:6: 'l2'
            {
            match("l2"); 



            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "L2"

    // $ANTLR start "L3"
    public final void mL3() throws RecognitionException {
        try {
            int _type = L3;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // C:\\Users\\admin\\Documents\\randompii\\Pint.g:5:4: ( 'l3' )
            // C:\\Users\\admin\\Documents\\randompii\\Pint.g:5:6: 'l3'
            {
            match("l3"); 



            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "L3"

    // $ANTLR start "P3"
    public final void mP3() throws RecognitionException {
        try {
            int _type = P3;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // C:\\Users\\admin\\Documents\\randompii\\Pint.g:6:4: ( 'p3' )
            // C:\\Users\\admin\\Documents\\randompii\\Pint.g:6:6: 'p3'
            {
            match("p3"); 



            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "P3"

    // $ANTLR start "S2"
    public final void mS2() throws RecognitionException {
        try {
            int _type = S2;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // C:\\Users\\admin\\Documents\\randompii\\Pint.g:7:4: ( 's2' )
            // C:\\Users\\admin\\Documents\\randompii\\Pint.g:7:6: 's2'
            {
            match("s2"); 



            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "S2"

    // $ANTLR start "S3"
    public final void mS3() throws RecognitionException {
        try {
            int _type = S3;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // C:\\Users\\admin\\Documents\\randompii\\Pint.g:8:4: ( 's3' )
            // C:\\Users\\admin\\Documents\\randompii\\Pint.g:8:6: 's3'
            {
            match("s3"); 



            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "S3"

    // $ANTLR start "S3R"
    public final void mS3R() throws RecognitionException {
        try {
            int _type = S3R;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // C:\\Users\\admin\\Documents\\randompii\\Pint.g:9:5: ( 's3r' )
            // C:\\Users\\admin\\Documents\\randompii\\Pint.g:9:7: 's3r'
            {
            match("s3r"); 



            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "S3R"

    // $ANTLR start "TX"
    public final void mTX() throws RecognitionException {
        try {
            int _type = TX;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // C:\\Users\\admin\\Documents\\randompii\\Pint.g:10:4: ( 't' )
            // C:\\Users\\admin\\Documents\\randompii\\Pint.g:10:6: 't'
            {
            match('t'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "TX"

    // $ANTLR start "INT"
    public final void mINT() throws RecognitionException {
        try {
            int _type = INT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // C:\\Users\\admin\\Documents\\randompii\\Pint.g:34:5: ( ( DIGIT )+ )
            // C:\\Users\\admin\\Documents\\randompii\\Pint.g:34:7: ( DIGIT )+
            {
            // C:\\Users\\admin\\Documents\\randompii\\Pint.g:34:7: ( DIGIT )+
            int cnt1=0;
            loop1:
            do {
                int alt1=2;
                int LA1_0 = input.LA(1);

                if ( ((LA1_0 >= '0' && LA1_0 <= '9')) ) {
                    alt1=1;
                }


                switch (alt1) {
            	case 1 :
            	    // C:\\Users\\admin\\Documents\\randompii\\Pint.g:
            	    {
            	    if ( (input.LA(1) >= '0' && input.LA(1) <= '9') ) {
            	        input.consume();
            	    }
            	    else {
            	        MismatchedSetException mse = new MismatchedSetException(null,input);
            	        recover(mse);
            	        throw mse;
            	    }


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

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "INT"

    // $ANTLR start "DIGIT"
    public final void mDIGIT() throws RecognitionException {
        try {
            // C:\\Users\\admin\\Documents\\randompii\\Pint.g:36:15: ( '0' .. '9' )
            // C:\\Users\\admin\\Documents\\randompii\\Pint.g:
            {
            if ( (input.LA(1) >= '0' && input.LA(1) <= '9') ) {
                input.consume();
            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;
            }


            }


        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "DIGIT"

    // $ANTLR start "DRAW"
    public final void mDRAW() throws RecognitionException {
        try {
            int _type = DRAW;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // C:\\Users\\admin\\Documents\\randompii\\Pint.g:38:6: ( 'd1' | 'd2' | 'd3' )
            int alt2=3;
            int LA2_0 = input.LA(1);

            if ( (LA2_0=='d') ) {
                switch ( input.LA(2) ) {
                case '1':
                    {
                    alt2=1;
                    }
                    break;
                case '2':
                    {
                    alt2=2;
                    }
                    break;
                case '3':
                    {
                    alt2=3;
                    }
                    break;
                default:
                    NoViableAltException nvae =
                        new NoViableAltException("", 2, 1, input);

                    throw nvae;

                }

            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 2, 0, input);

                throw nvae;

            }
            switch (alt2) {
                case 1 :
                    // C:\\Users\\admin\\Documents\\randompii\\Pint.g:38:8: 'd1'
                    {
                    match("d1"); 



                    }
                    break;
                case 2 :
                    // C:\\Users\\admin\\Documents\\randompii\\Pint.g:38:15: 'd2'
                    {
                    match("d2"); 



                    }
                    break;
                case 3 :
                    // C:\\Users\\admin\\Documents\\randompii\\Pint.g:38:22: 'd3'
                    {
                    match("d3"); 



                    }
                    break;

            }
            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "DRAW"

    // $ANTLR start "SEP"
    public final void mSEP() throws RecognitionException {
        try {
            int _type = SEP;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // C:\\Users\\admin\\Documents\\randompii\\Pint.g:40:5: ( ( '.' | '*' | '#' | '%' | '~' | '@' | '$' | '^' | '&' | ' ' | '\\t' | '\\n' | '\\r' | '\\u000C' )+ )
            // C:\\Users\\admin\\Documents\\randompii\\Pint.g:40:7: ( '.' | '*' | '#' | '%' | '~' | '@' | '$' | '^' | '&' | ' ' | '\\t' | '\\n' | '\\r' | '\\u000C' )+
            {
            // C:\\Users\\admin\\Documents\\randompii\\Pint.g:40:7: ( '.' | '*' | '#' | '%' | '~' | '@' | '$' | '^' | '&' | ' ' | '\\t' | '\\n' | '\\r' | '\\u000C' )+
            int cnt3=0;
            loop3:
            do {
                int alt3=2;
                int LA3_0 = input.LA(1);

                if ( ((LA3_0 >= '\t' && LA3_0 <= '\n')||(LA3_0 >= '\f' && LA3_0 <= '\r')||LA3_0==' '||(LA3_0 >= '#' && LA3_0 <= '&')||LA3_0=='*'||LA3_0=='.'||LA3_0=='@'||LA3_0=='^'||LA3_0=='~') ) {
                    alt3=1;
                }


                switch (alt3) {
            	case 1 :
            	    // C:\\Users\\admin\\Documents\\randompii\\Pint.g:
            	    {
            	    if ( (input.LA(1) >= '\t' && input.LA(1) <= '\n')||(input.LA(1) >= '\f' && input.LA(1) <= '\r')||input.LA(1)==' '||(input.LA(1) >= '#' && input.LA(1) <= '&')||input.LA(1)=='*'||input.LA(1)=='.'||input.LA(1)=='@'||input.LA(1)=='^'||input.LA(1)=='~' ) {
            	        input.consume();
            	    }
            	    else {
            	        MismatchedSetException mse = new MismatchedSetException(null,input);
            	        recover(mse);
            	        throw mse;
            	    }


            	    }
            	    break;

            	default :
            	    if ( cnt3 >= 1 ) break loop3;
                        EarlyExitException eee =
                            new EarlyExitException(3, input);
                        throw eee;
                }
                cnt3++;
            } while (true);


            _channel=HIDDEN;

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "SEP"

    // $ANTLR start "WORD"
    public final void mWORD() throws RecognitionException {
        try {
            int _type = WORD;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // C:\\Users\\admin\\Documents\\randompii\\Pint.g:42:6: ( ( CHAR )+ )
            // C:\\Users\\admin\\Documents\\randompii\\Pint.g:42:8: ( CHAR )+
            {
            // C:\\Users\\admin\\Documents\\randompii\\Pint.g:42:8: ( CHAR )+
            int cnt4=0;
            loop4:
            do {
                int alt4=2;
                int LA4_0 = input.LA(1);

                if ( ((LA4_0 >= 'A' && LA4_0 <= 'Z')||(LA4_0 >= 'a' && LA4_0 <= 'z')) ) {
                    alt4=1;
                }


                switch (alt4) {
            	case 1 :
            	    // C:\\Users\\admin\\Documents\\randompii\\Pint.g:
            	    {
            	    if ( (input.LA(1) >= 'A' && input.LA(1) <= 'Z')||(input.LA(1) >= 'a' && input.LA(1) <= 'z') ) {
            	        input.consume();
            	    }
            	    else {
            	        MismatchedSetException mse = new MismatchedSetException(null,input);
            	        recover(mse);
            	        throw mse;
            	    }


            	    }
            	    break;

            	default :
            	    if ( cnt4 >= 1 ) break loop4;
                        EarlyExitException eee =
                            new EarlyExitException(4, input);
                        throw eee;
                }
                cnt4++;
            } while (true);


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "WORD"

    // $ANTLR start "CHAR"
    public final void mCHAR() throws RecognitionException {
        try {
            // C:\\Users\\admin\\Documents\\randompii\\Pint.g:44:16: ( 'a' .. 'z' | 'A' .. 'Z' )
            // C:\\Users\\admin\\Documents\\randompii\\Pint.g:
            {
            if ( (input.LA(1) >= 'A' && input.LA(1) <= 'Z')||(input.LA(1) >= 'a' && input.LA(1) <= 'z') ) {
                input.consume();
            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;
            }


            }


        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "CHAR"

    public void mTokens() throws RecognitionException {
        // C:\\Users\\admin\\Documents\\randompii\\Pint.g:1:8: ( D4 | END | L2 | L3 | P3 | S2 | S3 | S3R | TX | INT | DRAW | SEP | WORD )
        int alt5=13;
        switch ( input.LA(1) ) {
        case '4':
            {
            int LA5_1 = input.LA(2);

            if ( (LA5_1=='d') ) {
                alt5=1;
            }
            else {
                alt5=10;
            }
            }
            break;
        case 'e':
            {
            int LA5_2 = input.LA(2);

            if ( ((LA5_2 >= 'A' && LA5_2 <= 'Z')||(LA5_2 >= 'a' && LA5_2 <= 'z')) ) {
                alt5=13;
            }
            else {
                alt5=2;
            }
            }
            break;
        case 'l':
            {
            switch ( input.LA(2) ) {
            case '2':
                {
                alt5=3;
                }
                break;
            case '3':
                {
                alt5=4;
                }
                break;
            default:
                alt5=13;
            }

            }
            break;
        case 'p':
            {
            int LA5_4 = input.LA(2);

            if ( (LA5_4=='3') ) {
                alt5=5;
            }
            else {
                alt5=13;
            }
            }
            break;
        case 's':
            {
            switch ( input.LA(2) ) {
            case '2':
                {
                alt5=6;
                }
                break;
            case '3':
                {
                int LA5_17 = input.LA(3);

                if ( (LA5_17=='r') ) {
                    alt5=8;
                }
                else {
                    alt5=7;
                }
                }
                break;
            default:
                alt5=13;
            }

            }
            break;
        case 't':
            {
            int LA5_6 = input.LA(2);

            if ( ((LA5_6 >= 'A' && LA5_6 <= 'Z')||(LA5_6 >= 'a' && LA5_6 <= 'z')) ) {
                alt5=13;
            }
            else {
                alt5=9;
            }
            }
            break;
        case '0':
        case '1':
        case '2':
        case '3':
        case '5':
        case '6':
        case '7':
        case '8':
        case '9':
            {
            alt5=10;
            }
            break;
        case 'd':
            {
            int LA5_8 = input.LA(2);

            if ( ((LA5_8 >= '1' && LA5_8 <= '3')) ) {
                alt5=11;
            }
            else {
                alt5=13;
            }
            }
            break;
        case '\t':
        case '\n':
        case '\f':
        case '\r':
        case ' ':
        case '#':
        case '$':
        case '%':
        case '&':
        case '*':
        case '.':
        case '@':
        case '^':
        case '~':
            {
            alt5=12;
            }
            break;
        case 'A':
        case 'B':
        case 'C':
        case 'D':
        case 'E':
        case 'F':
        case 'G':
        case 'H':
        case 'I':
        case 'J':
        case 'K':
        case 'L':
        case 'M':
        case 'N':
        case 'O':
        case 'P':
        case 'Q':
        case 'R':
        case 'S':
        case 'T':
        case 'U':
        case 'V':
        case 'W':
        case 'X':
        case 'Y':
        case 'Z':
        case 'a':
        case 'b':
        case 'c':
        case 'f':
        case 'g':
        case 'h':
        case 'i':
        case 'j':
        case 'k':
        case 'm':
        case 'n':
        case 'o':
        case 'q':
        case 'r':
        case 'u':
        case 'v':
        case 'w':
        case 'x':
        case 'y':
        case 'z':
            {
            alt5=13;
            }
            break;
        default:
            NoViableAltException nvae =
                new NoViableAltException("", 5, 0, input);

            throw nvae;

        }

        switch (alt5) {
            case 1 :
                // C:\\Users\\admin\\Documents\\randompii\\Pint.g:1:10: D4
                {
                mD4(); 


                }
                break;
            case 2 :
                // C:\\Users\\admin\\Documents\\randompii\\Pint.g:1:13: END
                {
                mEND(); 


                }
                break;
            case 3 :
                // C:\\Users\\admin\\Documents\\randompii\\Pint.g:1:17: L2
                {
                mL2(); 


                }
                break;
            case 4 :
                // C:\\Users\\admin\\Documents\\randompii\\Pint.g:1:20: L3
                {
                mL3(); 


                }
                break;
            case 5 :
                // C:\\Users\\admin\\Documents\\randompii\\Pint.g:1:23: P3
                {
                mP3(); 


                }
                break;
            case 6 :
                // C:\\Users\\admin\\Documents\\randompii\\Pint.g:1:26: S2
                {
                mS2(); 


                }
                break;
            case 7 :
                // C:\\Users\\admin\\Documents\\randompii\\Pint.g:1:29: S3
                {
                mS3(); 


                }
                break;
            case 8 :
                // C:\\Users\\admin\\Documents\\randompii\\Pint.g:1:32: S3R
                {
                mS3R(); 


                }
                break;
            case 9 :
                // C:\\Users\\admin\\Documents\\randompii\\Pint.g:1:36: TX
                {
                mTX(); 


                }
                break;
            case 10 :
                // C:\\Users\\admin\\Documents\\randompii\\Pint.g:1:39: INT
                {
                mINT(); 


                }
                break;
            case 11 :
                // C:\\Users\\admin\\Documents\\randompii\\Pint.g:1:43: DRAW
                {
                mDRAW(); 


                }
                break;
            case 12 :
                // C:\\Users\\admin\\Documents\\randompii\\Pint.g:1:48: SEP
                {
                mSEP(); 


                }
                break;
            case 13 :
                // C:\\Users\\admin\\Documents\\randompii\\Pint.g:1:52: WORD
                {
                mWORD(); 


                }
                break;

        }

    }


 

}