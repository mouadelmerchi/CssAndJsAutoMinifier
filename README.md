# Css & Js Auto-Minifier
JavaFX program to minify all CSS and/or JavaScript files inside a directory.

**It doesn't make use of cssminifier.com and javascript-minifier.com services.**

Without the dependency of online services, it can be used offline.

Current version: 1.0.0

# What can we do with it?
- Select a directory containing CSS/JS files.
- Filter your search by specifying whether you want to process the selected directory recursively.
- Filter your search by specifying whether you want to compress CSS files, JS files or both.
- Compress found files manually.
- Enable automatic mode where the selected directory is watched for changes made to CSS/JS files. With this mode on,
  you can edit your CSS/JS files in your favorite IDE and the program will take care of the compressing automatically.
- Choose your minification settings (ex. minification extension like .min or -min, Enable/Disable obfuscation of js variables, etc.)

# Program UI
![CSS & JS Auto-minifier](https://github.com/mouadelmerchi/CssAndJsAutoMinifier/blob/master/GUI.PNG)

# Used APIs:
- [JavaFX 8 and ControlsFX 8](http://fxexperience.com/controlsfx/)
- [YUI Compressor](http://yui.github.io/yuicompressor/) (Provides offline minification APIs for css and js files)
- [Gson](https://github.com/google/gson) (Java library to manipulate JSON in java. Used to manage user settings)
- [Apache Tike](https://tika.apache.org/) (Library used to detect scanned file's content types)
- [Apache Commons APIs](https://commons.apache.org/)

# TODO
- Regenerate compressed files after deletion with automatic mode on.
  
# License
CSS & Js Auto-Minifier
Copyright (&copy;) 2017  Mouad El Merchichi

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
