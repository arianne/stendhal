/*
	sign4j.c: a simple utility to sign executables created by Launch4j

	Copyright (c) 2012 Servoy
	All rights reserved.

	Redistribution and use in source and binary forms, with or without modification,
	are permitted provided that the following conditions are met:
	
	1. Redistributions of source code must retain the above copyright notice,
	   this list of conditions and the following disclaimer.
	
	2. Redistributions in binary form must reproduce the above copyright notice,
	   this list of conditions and the following disclaimer in the documentation
	   and/or other materials provided with the distribution.
	
	3. Neither the name of the copyright holder nor the names of its contributors
	   may be used to endorse or promote products derived from this software without
	   specific prior written permission.
	
	THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
	AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
	THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
	ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
	FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
	(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
	LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED
	AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
	OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
	OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/

#include <stdlib.h>
#include <string.h>
#include <stdio.h>
#ifdef _WIN32
#include <io.h>
#include <fcntl.h>
#else
#include <sys/io.h>
#include <sys/fcntl.h>
#endif
#include <sys/types.h>
#include <sys/stat.h>

#define ZIP_END_HEADER     "\x50\x4B\x05\x06"
#define END_HEADER_SIZE    22
#define MAX_COMMENT_SIZE   0xFFFF
#define SWAP_BLOCK_SIZE    (4 * 1024 * 1024)
#define TEST_FILE_NAME     "sign4j_temporary.exe"
#define SIGN4J_VERSION     "3.0"

#ifndef _WIN32
#define O_BINARY 0
#define _O_SHORT_LIVED 0
#define _S_IREAD S_IREAD
#define _S_IWRITE S_IWRITE
#define stricmp strcasecmp
#endif

typedef unsigned char byte;

char  command[4096];
byte* image = 0;

void usage (void);
void quit (int rsn);
void clear (void);

int main (int argc, char* argv[])
{
   char  bfr[2];
   char* inf;
   char* outf;
   char* trg;
   byte* lmt;
   long  lng, ext, off, blck, sgm;
   int   fd, td;
   int   fnd, spt, unq, vrb, qts, cmn;
   int   i, j, n;
   byte* p;

   inf = outf = 0, fnd = spt = unq = vrb = 0;
   for (i = 1; i < argc && argv[i][0] == '-'; i++)
      if (! strcmp (argv[i], "--onthespot")) 
         spt = 1;
      else if (! strcmp (argv[i], "--strict")) 
         unq = 1;
      else if (! strcmp (argv[i], "--verbose")) 
         vrb = 1;
   j = i;
   for (i = j + 1; i < argc; i++)
      if (! strcmp (argv[i], "-in") && i < argc - 1) 
         inf = argv[++i], fnd = 1;
      else if (! strcmp (argv[i], "-out") && i < argc - 1) 
         outf = argv[++i], fnd = 1;
      else if (argv[i][0] == '-' || (argv[i][0] == '/' && strlen (argv[i]) < 5))
         (! fnd ? (inf = outf = 0) : 0);
      else if (! fnd && (n = strlen (argv[i])) > 4 && ! stricmp (argv[i] + n - 4, ".exe"))
         inf = outf = argv[i];
   if (! inf || ! outf)
      usage ();
   atexit (clear);

   if ((fd = open (inf, O_RDONLY | O_BINARY)) < 0)
      quit (1);
   if ((lng = lseek (fd, 0, SEEK_END)) < 0)
      quit (2);
   blck = (lng > SWAP_BLOCK_SIZE ? SWAP_BLOCK_SIZE : lng);
   if (! (image = (byte*) malloc (blck)))
      quit (4);
   sgm = (blck > END_HEADER_SIZE + MAX_COMMENT_SIZE ? END_HEADER_SIZE + MAX_COMMENT_SIZE : blck);
   if (lseek (fd, -sgm, SEEK_END) < 0 || read (fd, image, sgm) != sgm)
      quit (2);
   for (p = image + sgm - END_HEADER_SIZE; p > image; p--)
      if (! memcmp (p, ZIP_END_HEADER, 4) && ((p[END_HEADER_SIZE - 1] << 8) | p[END_HEADER_SIZE - 2]) == (image + sgm) - (p + END_HEADER_SIZE))
         break;
   if (p > image)
   {
      off = lng - ((image + sgm) - (p + END_HEADER_SIZE - 2));
      cmn = (p[END_HEADER_SIZE - 1] << 8) | p[END_HEADER_SIZE - 2];

      if (! spt && (inf == outf || ! strcmp (inf, outf)))
      {
         printf ("Making temporary file\n");
         if ((td = open (TEST_FILE_NAME, O_CREAT | _O_SHORT_LIVED | O_WRONLY | O_BINARY, _S_IREAD | _S_IWRITE)) < 0)
            quit (5);
         if (lseek (fd, 0, SEEK_SET) < 0)
            quit (2);
         for (ext = lng; ext > 0; ext -= blck)
         {
            sgm = (ext > blck ? blck : ext);
            if (read (fd, image, sgm) != sgm || write (td, image, sgm) != sgm)
               quit (6);
         }
         close (td);
         trg = TEST_FILE_NAME;
      }
      else
         trg = outf;
      close (fd);

#ifdef _WIN32
      strcpy (command,  "\" ");
#else
      strcpy (command, "");
#endif

      for (i = j; i < argc; i++)
      {
         p = (argv[i] == outf ? trg : argv[i]);
         qts = (! unq || strchr (p, 32));
         if (qts)
            strcat (command, "\"");
         strcat (command, p);
         if (qts)
            strcat (command, "\"");
         strcat (command, " ");
      }

#ifdef _WIN32
      strcat (command, "\"");
#endif

      if (! vrb)
#ifdef _WIN32
         strcat (command, " > NUL");
#else
         strcat (command, " > /dev/null");
#endif

      system (command);

      if ((td = open (trg, O_RDONLY | O_BINARY)) < 0)
         quit (7);
      if ((ext = lseek (td, 0, SEEK_END)) < 0)
         quit (7);
      close (td);
      if ((cmn += ext - lng) < 0 || cmn > MAX_COMMENT_SIZE)
         quit (8);

      if ((fd = open (inf, O_WRONLY | O_BINARY)) < 0)
         quit (1);
      if (lseek (fd, off, SEEK_SET) < 0)
         quit (3);
      bfr[0] = cmn & 0xFF;
      bfr[1] = (cmn >> 8) & 0xFF;
      if (write (fd, bfr, 2) != 2)
         quit (3);
      close (fd);
   }
   else
   {
      close (fd);
      printf ("You don't need sign4j to sign this file\n");
   }

#ifdef _WIN32
   strcpy (command,  "\" ");
#else
   strcpy (command, "");
#endif

   for (i = j; i < argc; i++)
   {
      p = argv[i];
      qts = (! unq || strchr (p, 32));
      if (qts)
         strcat (command, "\"");
      strcat (command, p);
      if (qts)
         strcat (command, "\"");
      strcat (command, " ");
   }

#ifdef _WIN32
   strcat (command, "\"");
#endif

   return system (command);
}


void usage ()
{
   printf ("\nThis is sign4j version " SIGN4J_VERSION "\n\n");
   printf ("Usage: sign4j [options] <arguments>\n\n");
   printf (" * options:\n");
   printf ("    --onthespot   avoid the creation of a temporary file (your tool must be able to sign twice)\n");
   printf ("    --strict      supress the use of double quotes around parameters that strictly don't need them\n");
   printf ("    --verbose     show diagnostics about intermediary steps of the process\n");
   printf (" * arguments must specify verbatim the command line for your signing tool\n");
   printf (" * only one file can be signed on each invocation\n");
   exit (-1);
}

void quit (int rsn)
{
   switch (rsn)
   {
   case 1: puts ("Could not open file\n"); break;
   case 2: puts ("Could not read file\n"); break;
   case 3: puts ("Could not write file\n"); break;
   case 4: puts ("Not enough memory\n"); break;
   case 5: puts ("Could not open temporary\n"); break;
   case 6: puts ("Could not write temporary\n"); break;
   case 7: puts ("Could not read target\n"); break;
   case 8: puts ("Unsupported operation\n"); break;
   }
   exit (-1);
}

void clear ()
{
   if (access (TEST_FILE_NAME, 0) == 0)
      remove (TEST_FILE_NAME);
   if (image)
      free (image);
}
