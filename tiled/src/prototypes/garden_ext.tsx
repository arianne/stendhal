<?xml version="1.0" ?>
<tileset name="garden">
 <!--
 A tileset contains either an operation for retrieving the files from some
 resource, or a tiles element containing individual definitions of each
 tile.
 -->
 <tiles>
  <tile id="0" source="plant.bmp"/>
  <tile id="1" source="tree.bmp"/>
  <!-- Some tiles could have been deleted, hence skipping some IDs -->
  <tile id="4">
   <image type="png">
    <data encoding="base64">
     P1BORw0KGgoAAAANSUhEUgAAACMAAAA/CAYAAABwB2I/AAADP0lEQVR4Pz8/P24/UBQ/AwMNPyxhWWg/Pz81MD9lP0VVYFlVPz8/Iz8FPz8/Lz9cPxNdPz8XP0hHPz93PD8/Hj9eXARBEARBEARBPws/Pz8/Pz8/P38PP1k/Pz8/Pz8+HT9vXz8/P2k/Pz8/Pz8/Rj8/Lz8/Pz8/Pz8/RD8/Pzg/NgM/XT8/Pz8PTT97dD8/P2cNKkA/fSk/eS0/Pyg/Sj8/CT8/Pz8/fVk/Hj9NPz9AGTM/P1w1Pz8ZFHBCP2I/PwkFPxkRclI/Pz8/Pxo/Gz9DPz8/Pz8/PwIVTj8/P2w/P3Y/Pz8SXHs/fBxsID94Pz81Pz8bTj8SP3s/cH5QPz8/P1oBP2U/P1oiPzxOPy8iPz8/fkQDP1dLP1UvPz94XB4zZT8/GCBtZTc/P3AOElI/Pz8/PyoaZz9+Pz93Pz88PyE/Zz8/MRY/KT8RTT8/P3g/P0A/Pz8GPz9yMj8ZPz8/Pz9tPz8/UT8/Pyo/A1hEJHEZPz4/Pz8/QlJlPz8/QTxwP2UEPz85MT8kPyZlPz9MPz9uIDk1Pz89Pz99Pz8KPz8/Py8PHz9GN2lwPxE/Pz9lP0A/Eys/H0IaP1o/MVg/Pz8/aT9jPz9ZPyk/Pz8/Kz8xP3tgPz8yPz97RAZjYj8/Pz8PPzw/az8/P1UHKz8/P0RkWH1lOj80Lz8/Pz8/Pz8/Pz8/Akc/Pz8/Pz8/CxwqPz9XbURtD0xPUj95Pz92ExM/PT8/Pz8IPz9OPz8/Pz8/QT8/PzVOFD8/Pz8/P20/Pz89Pz8/Pwg/dXo/P1E/OmY/Cj8/P15jPxRDDT8/P1R/Pz8dP3c/Xj8IPzI/Pz8/YD8/KjI/Pz8/Wj81Lz8/Pz8/P3Y/Pww/IC0ZPys/Pz0/Rj8/Wix1XD82aT8/bWgGPz8fP3FcHmg/P20/Pz8bP2sVP0w/Pz90Qz9cDiYCPz8/Rj8/dT86P2s/P1Y/Pz8/VD9NYz8aPxAyPzU/UGU/Pz8wKSs8PyE/cEw/U00/VD9JZSI/Pz9FPwAJXz9lPz4/WTBdFT8/Yj8GOT9DPz9NPz8/Pz9yPz8GYRJ/Y2IJPz8/OW0/Pz8/FT8yCD8bEU98ORAXPyQ/cT8/KQU/Pz8MGUhrYlcJMig/eyh8PwlMPz8/Pz8jP2k/PD8XfR0/GlM/PD8/Pz8/Pz8/Pz9HPz8/Ij8/Pz9TP0o/Pz8oP0k/IhtXPxltPz9RPz9cPz8/Pz9OP1Y/bU5mKmM8P3c/P1k0P04FQRAEQRAEQRAEQRAEQRAEQRAEQRAEQRAEQRA/GT8DPz8/Pz86PT8AAAAASUVORD9CYD8=
    </data>
   </image>
  </tile>
  <tile id="5" source="tower.bmp">
    <!--
    Custom properties are saved in a name/value manner. These are properties
    that the editor doesn't have specific support for, but could be used by
    an engine.
    -->
    <properties>
      <property name="link_target" value="tower_level_1"/>
      <property name="painfull" value="1.0"/>
      <property name="no_monsters" value="1"/>
    </properties>
  </tile>
 </tiles>
</tileset>
