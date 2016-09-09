
import java.io.*;
import java.util.Stack;

public class CommentGlide
{
	public static void main(String[] argv)
	{
		if (argv.length!=1) {
			System.err.println("usage: bin/java {this} <filename>");
			System.exit(-1);
		}

      new CommentGlide().glide(argv[0]);
	}

	public CommentGlide()
	{
	}

	public void glide(String fileName)
	{
		// the in stream
		BufferedReader in = null;

		// the out stream
		BufferedWriter out = null;

		try {

			// our buffer
			Stack tmp = new Stack();

			// open the stream on the filename
			in = new BufferedReader(
					new FileReader(fileName));

			// parse it into the buffer
			read(in, tmp);

			// clean up
			in.close();

			// open the stream on the filename
			out = new BufferedWriter(
					 new FileWriter(fileName));

			// write it our from the buffer
			write(out, tmp);

			 // now open an
			// clean up
			out.flush(); out.close();

		} catch (IOException ex) { ex.printStackTrace();
		} finally {

			// clean up in if necessary
			if (in!=null)
				try { in.close(); } catch (Exception ex) {}

			// clean up out if necessary
			if (out!=null)
				try { out.close(); } catch (Exception ex) {}

		}
	}

	private void write(BufferedWriter out, Stack tmp)
	throws IOException
	{
		while(!tmp.empty())
		{
			out.write((String)tmp.pop());
		}
	}

	private void read(BufferedReader in, Stack tmp)
	throws IOException
	{
		 // ---------------
		// each line
		String line = null;

		 // ---------------
		// our Line references
		Line previous = null;
		Line current = null;
		Line last = null;

		 // read each line and make a line object.
		while ((line = in.readLine()) != null)
			// make a new line and set is as previous
			previous = new Line(line, previous);

		  // now switch gears.  We want to go the other way.
		 // by this we add one more dummy line to make
		// the loop balanced.  This one never gets written.
		last = new Line("", previous);

		// iterate back over the elements
		do {
			// assign the current reference to the previous
			current = last.previous;

          // now check if the last was a comment
         // and that this one is as well
         if (last.isComment() && current.isComment())
				  // the condition is satisfied.  We then write
				 // the current one to the output, having it
				// synchronize itself to the last leader.
         	current.write(tmp, last.leader);
         else
				   // either the last one was not a comment,
				  // or this one is not a comment.  Either
				 // was, we're not going to want to indent this
				// one.
         	current.write(tmp);

			 // final thing is assign the current
			// to the last ref
			last = current;

		} while (last.previous != null);
	}

	private static class Line
	{
		Line(String line, Line previous)
		{
			this.previous = previous;
			split(line);
		}

		private void split(String line)
		{
         // the line char array
         char[] chars = line.toCharArray();
         int len = chars.length;
         int count=0;

			// count the number of whitespace chars, making sure we don't overflow
			while (count<len && Character.isWhitespace(chars[count]))
				count++;

			// make a new string of the whitespace
			leader = new String(chars, 0, count);
			// make a new string of the non-whitespace
			trailer = new String(chars, count, (len-count));

		}

		public void write(Stack tmp)
		{
			tmp.push(leader+trailer+NEWLINE);
		}

		public void write(Stack tmp, String lastLeader)
		{
			    // if the last leader is not null, we interpret this
			   // to mean that is was a comment.  We rely on the calling
			  // method to get this right.  If we are a comment also,
			 // then we synchronize ourself with the last one, plus
			// a space.
			if (isComment())
				this.leader = lastLeader + ' ';

			write(tmp);
		}

		private boolean isComment()
		{
			return trailer.startsWith("//");
		}

		public String leader;
		public String trailer;
		public Line previous;
		private static String NEWLINE = System.getProperty("line.separator");
	}
}

