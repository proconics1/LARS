<?xml version="1.0" encoding="ISO-8859-1"?>
<xsl:stylesheet version="1.0" 
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
	xmlns:fo="http://www.w3.org/1999/XSL/Format" 
	xmlns:java="http://xml.apache.org/xslt/java"
    xmlns:xlink="http://www.w3.org/1999/xlink"
    xmlns:svg="http://www.w3.org/2000/svg"
	exclude-result-prefixes="fo">
	<xsl:output method="xml" version="1.0" omit-xml-declaration="no" indent="yes" />
	<xsl:template match="/">
		<fo:root xmlns:fo="http://www.w3.org/1999/XSL/Format">
			<fo:layout-master-set>
				<fo:simple-page-master margin-bottom="0.79in" margin-left="0.98in" margin-right="0.98in" margin-top="0.25in" master-name="s1-default" page-height="210mm" page-width="297mm">
					<fo:region-body margin-left="0mm" margin-right="0mm" margin-top="15mm" margin-bottom="8mm"/>
					<fo:region-before extent="12mm" region-name="xsl-region-before-default"/>
					<fo:region-after extent="12mm" region-name="xsl-region-after-default"/>
				</fo:simple-page-master>
				<fo:page-sequence-master master-name="s1">
					<fo:repeatable-page-master-alternatives>
						<fo:conditional-page-master-reference master-reference="s1-default"/>
					</fo:repeatable-page-master-alternatives>
				</fo:page-sequence-master>
			</fo:layout-master-set>
		
			<fo:page-sequence id="section_s1" format="" master-reference="s1">
				<fo:static-content flow-name="xsl-region-before-default">
					<fo:block font-size="11.0pt" line-height="100%" space-after="0in" space-before="0in" text-align="center">
						<inline xmlns="http://www.w3.org/1999/XSL/Format" font-family="Calibri" padding-right="2cm">Bericht</inline>
						<inline xmlns="http://www.w3.org/1999/XSL/Format" font-family="Calibri">LARS ICS - Light And Right Security ICS</inline>
						<inline xmlns="http://www.w3.org/1999/XSL/Format" font-family="Calibri" padding-left="2cm"><xsl:value-of select="report/date" /></inline>
					</fo:block>
				</fo:static-content>
				<fo:static-content flow-name="xsl-region-after-default">
					<fo:block font-size="11.0pt" line-height="100%" space-after="0in" space-before="0in" text-align="center" font-family="Calibri">
						<fo:leader rule-thickness="0.5pt" rule-style="solid" leader-length="100%" leader-pattern="rule"/>
						Seite <fo:page-number/> von <fo:page-number-citation ref-id="theEnd"/> 
					</fo:block>
				</fo:static-content>
				<fo:flow flow-name="xsl-region-body">
		
					<xsl:apply-templates />
		
					<fo:block id="theEnd"/>
				</fo:flow>
			</fo:page-sequence>
		
		</fo:root>
	</xsl:template>
	<xsl:template match="report">
		<fo:block break-before="auto" font-size="16.0pt" font-weight="bold" keep-with-next="always" line-height="115%" space-after="1mm" space-before="4mm" text-align="center">
			<inline xmlns="http://www.w3.org/1999/XSL/Format" font-family="Cambria"><xsl:value-of select="name" /></inline>
		</fo:block>

		<fo:block font-size="11.0pt" line-height="115%" space-after="4mm" space-before="4mm">
			<inline xmlns="http://www.w3.org/1999/XSL/Format" font-weight="bold">
				<inline font-family="Calibri">Projekt: </inline>
			</inline>
			<inline xmlns="http://www.w3.org/1999/XSL/Format" font-family="Calibri"> </inline>
			<inline xmlns="http://www.w3.org/1999/XSL/Format" font-family="Calibri"><xsl:value-of select="case" /></inline>
		</fo:block>

		<fo:block font-size="11.0pt" line-height="115%" space-after="4mm" space-before="0in">
			<inline xmlns="http://www.w3.org/1999/XSL/Format" font-weight="bold">
				<inline font-family="Calibri">Benutzer: </inline>
			</inline>
			<inline xmlns="http://www.w3.org/1999/XSL/Format" font-family="Calibri"> </inline>
			<inline xmlns="http://www.w3.org/1999/XSL/Format" font-family="Calibri"><xsl:value-of select="user" /></inline>
		</fo:block>

		<fo:block font-size="11.0pt" line-height="115%" space-after="4mm" space-before="0in"  font-family="Calibri">
			<xsl:apply-templates />
		</fo:block>
	</xsl:template>
		
		<xsl:template match="measures">
			<xsl:choose>
				<xsl:when test="priority">
					<xsl:apply-templates />
				</xsl:when>
				<xsl:otherwise>
					<fo:block>
						Keine Maßnahmen gefunden, die den Kriterien entsprechen.
					</fo:block>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:template>
		
			<xsl:template match="priority">
				<fo:block font-size="12.0pt" line-height="115%" space-after="2mm" space-before="4mm">
					<inline xmlns="http://www.w3.org/1999/XSL/Format" font-weight="bold">
						<inline font-family="Calibri">Priorität <xsl:value-of select="value" />:</inline>
					</inline>
				</fo:block>
				<fo:block>
					<!--fo:table-and-caption-->
					<fo:table table-layout="fixed" width="auto">
					<!--fo:table-column column-number="1" /-->
					<!--fo:table-column column-number="2" /-->
					<!--fo:table-column column-number="3" /-->
					<!--fo:table-column column-number="4" /-->
					<!--fo:table-column column-number="5" /-->
				
						<fo:table-header>
						  <fo:table-row>
							<fo:table-cell>
							  <fo:block font-weight="bold">Asset Typ</fo:block>
							</fo:table-cell>
							<fo:table-cell>
							  <fo:block font-weight="bold">Domäne</fo:block>
							</fo:table-cell>
							<fo:table-cell>
							  <fo:block font-weight="bold">Kategorie</fo:block>
							</fo:table-cell>
							<fo:table-cell>
							  <fo:block font-weight="bold">Maßnahme</fo:block>
							</fo:table-cell>
							<fo:table-cell>
							  <fo:block font-weight="bold">Kritikalität</fo:block>
							</fo:table-cell>
							<fo:table-cell>
							  <fo:block font-weight="bold">Antwort</fo:block>
							</fo:table-cell>
						  </fo:table-row>
						</fo:table-header>
						
						<fo:table-body>
							<xsl:apply-templates />
						</fo:table-body>
					</fo:table>
					<!--/fo:table-and-caption-->
				</fo:block>
			</xsl:template>
			
			<xsl:template match="unanswered">
				<fo:block font-size="12.0pt" line-height="115%" space-after="2mm" space-before="4mm">
					<inline xmlns="http://www.w3.org/1999/XSL/Format" font-weight="bold">
						<inline font-family="Calibri">Nicht beantwortete Fragen:</inline>
					</inline>
				</fo:block>
				<fo:block>
					<!--fo:table-and-caption-->
					<fo:table table-layout="fixed" width="auto">
					<!--fo:table-column column-number="1" /-->
					<!--fo:table-column column-number="2" /-->
					<!--fo:table-column column-number="3" /-->
					<!--fo:table-column column-number="4" /-->
					<!--fo:table-column column-number="5" /-->
				
						<fo:table-header>
						  <fo:table-row>
							<fo:table-cell>
							  <fo:block font-weight="bold">Asset Typ</fo:block>
							</fo:table-cell>
							<fo:table-cell>
							  <fo:block font-weight="bold">Domäne</fo:block>
							</fo:table-cell>
							<fo:table-cell>
							  <fo:block font-weight="bold">Kategorie</fo:block>
							</fo:table-cell>
							<fo:table-cell>
							  <fo:block font-weight="bold">Maßnahme</fo:block>
							</fo:table-cell>
							<fo:table-cell>
							  <fo:block font-weight="bold">Kritikalität</fo:block>
							</fo:table-cell>
							<fo:table-cell>
							  <fo:block font-weight="bold">Antwort</fo:block>
							</fo:table-cell>
						  </fo:table-row>
						</fo:table-header>
						
						<fo:table-body>
							<xsl:apply-templates />
						</fo:table-body>
					</fo:table>
					<!--/fo:table-and-caption-->
				</fo:block>
			</xsl:template>
	
				<xsl:template match="measure">
					<xsl:if test="measureanswer/complex != 'umgesetzt'">
						<fo:table-row>
							<fo:table-cell>
								<fo:block>
									<xsl:value-of select="assettype" />
								</fo:block>
							</fo:table-cell>
							<fo:table-cell>
								<fo:block>
									<xsl:value-of select="domain" />
								</fo:block>
							</fo:table-cell>
							<fo:table-cell>
								<fo:block>
									<xsl:value-of select="category" />
								</fo:block>
							</fo:table-cell>
							<fo:table-cell>
								<fo:block>
									<xsl:value-of select="name" />
								</fo:block>
							</fo:table-cell>
							<fo:table-cell>
								<fo:block>
									<xsl:value-of select="measurescore" />
								</fo:block>
							</fo:table-cell>
							<xsl:apply-templates />
						</fo:table-row>
					</xsl:if>
				</xsl:template>
				
					<xsl:template match="measureanswer">
							<fo:table-cell>
								<fo:block>
									<xsl:value-of select="complex" />
								</fo:block>
							</fo:table-cell>
					</xsl:template>
			
	 <xsl:template match="answerstatistic">
	    <fo:block break-before="auto" font-size="14.0pt" keep-with-next="always" line-height="115%" space-after="1mm" space-before="4mm" text-align="center">
			<inline xmlns="http://www.w3.org/1999/XSL/Format" font-weight="bold" font-family="Cambria">Umsetzungsstatus der Maßnahmen</inline>
		</fo:block>
	    <fo:block>
	 	<fo:instream-foreign-object>
	     <svg:svg width="500" height="300" viewBox="0 0 500 300">
	     <xsl:variable name="total" select="sum(pieslice)"/>
	     <xsl:for-each select="pieslice">
	          <xsl:variable name="color">
	               <xsl:choose>
	                    <xsl:when test="(position() = 1)">
	                         <xsl:text>green</xsl:text>
	                    </xsl:when>
	                    <xsl:when test="(position() = 2)">
	                         <xsl:text>yellow</xsl:text>
	                    </xsl:when>
	                    <xsl:when test="(position() = 3)">
	                         <xsl:text>red</xsl:text>
	                    </xsl:when>
	                    <xsl:when test="(position() = 4)">
	                         <xsl:text>darkred</xsl:text>
	                    </xsl:when>
	                    <xsl:when test="(position() = 5)">
	                         <xsl:text>grey</xsl:text>
	                    </xsl:when>
	                    <xsl:otherwise>
	                         <xsl:text>lightSteelBlue</xsl:text>
	                    </xsl:otherwise>
	               </xsl:choose>
	          </xsl:variable>
			  <xsl:variable name="position" select="position()"/>
	          
	          <xsl:apply-templates select=".">
	               <xsl:with-param name="color" select="$color"/>
	               <xsl:with-param name="total" select="$total"/>
	               <xsl:with-param name="runningTotal" select = "sum(preceding-sibling::pieslice)"/>
	          </xsl:apply-templates>
	
	          <xsl:apply-templates select="." mode="legend">
	               <xsl:with-param name="color" select="$color"/>
	               <xsl:with-param name="offset" select="90 + ($position * 20)"/>
	          </xsl:apply-templates>
	
	     </xsl:for-each>
	     </svg:svg>
	 	</fo:instream-foreign-object>
	 	</fo:block>
	</xsl:template>
	<xsl:template match="scorestatistic">
		<fo:block break-before="auto" font-size="14.0pt" keep-with-next="always" line-height="115%" space-after="1mm" space-before="4mm" text-align="center">
			<inline xmlns="http://www.w3.org/1999/XSL/Format" font-weight="bold" font-family="Cambria"><xsl:value-of select="answer" />e Maßnahmen</inline>
		</fo:block>
	    <fo:block>
	 	<fo:instream-foreign-object>
	     <svg:svg width="500" height="300" viewBox="0 0 500 300">
	     <xsl:variable name="total" select="sum(pieslice)"/>
	     <xsl:for-each select="pieslice">
	          <xsl:variable name="color">
	               <xsl:choose>
	                    <xsl:when test="(position() = 1)">
	                         <xsl:text>darkred</xsl:text>
	                    </xsl:when>
	                    <xsl:when test="(position() = 2)">
	                         <xsl:text>red</xsl:text>
	                    </xsl:when>
	                    <xsl:when test="(position() = 3)">
	                         <xsl:text>orange</xsl:text>
	                    </xsl:when>
	                    <xsl:when test="(position() = 4)">
	                         <xsl:text>yellow</xsl:text>
	                    </xsl:when>
	                    <xsl:when test="(position() = 5)">
	                         <xsl:text>gray</xsl:text>
	                    </xsl:when>
	                    <xsl:otherwise>
	                         <xsl:text>lightSteelBlue</xsl:text>
	                    </xsl:otherwise>
	               </xsl:choose>
	          </xsl:variable>
			  <xsl:variable name="position" select="position()"/>
	          
	          <xsl:apply-templates select=".">
	               <xsl:with-param name="color" select="$color"/>
	               <xsl:with-param name="total" select="$total"/>
	               <xsl:with-param name="runningTotal" select = "sum(preceding-sibling::pieslice)"/>
	          </xsl:apply-templates>
	
	          <xsl:apply-templates select="." mode="legend">
	               <xsl:with-param name="color" select="$color"/>
	               <xsl:with-param name="offset" select="90 + ($position * 20)"/>
	          </xsl:apply-templates>
	
	     </xsl:for-each>
	     </svg:svg>
	 	</fo:instream-foreign-object>
	 	</fo:block>
	</xsl:template>
	<xsl:template match="pieslice">
	    <xsl:param name="color" select="'indianRed'"/>
	    <xsl:param name="total" select="'0'"/>
	    <xsl:param name="runningTotal" select="'0'"/>
	    <xsl:variable name="number" select="."/>
	    <xsl:variable name="tmpAngle" select="java:java.lang.Math.toRadians(($number div $total) * 360.0)"/>
		<xsl:variable name="currentAngle">
			<xsl:choose>
				<xsl:when test="$tmpAngle = (6.283185307179586)">
					<xsl:value-of select="6.283185207179586"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="$tmpAngle"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
	    <xsl:variable name="halfAngle" select="java:java.lang.Math.toRadians((($number div 2) div $total) * 360.0)"/>
	    <xsl:variable name="rotation" select="270 + (360.0 * ($runningTotal div $total))"/>
	    <xsl:variable name="x1" select="java:java.lang.Math.cos($halfAngle) * 70"/>
	    <xsl:variable name="y1" select="java:java.lang.Math.sin($halfAngle) * 70"/>
	    <xsl:variable name="cosTheta" select="java:java.lang.Math.cos(java:java.lang.Math.toRadians($rotation))"/>
	    <xsl:variable name="sinTheta" select="java:java.lang.Math.sin(java:java.lang.Math.toRadians($rotation))"/>
		<svg:path style="fill:{$color};stroke:black;stroke-width:1; fillrule:evenodd;stroke-linejoin:bevel;">
        	<xsl:attribute name="transform">
        		<xsl:text>translate(150,150)</xsl:text> <!--centering the circle in the non-legend space-->
        		<xsl:text>rotate(</xsl:text>
        		<xsl:value-of select="$rotation"/>
        		<xsl:text>)</xsl:text>
        	</xsl:attribute>
        	<xsl:attribute name="d">
            	<xsl:text>M 100 0 A 100 100 0 </xsl:text> <!-- to change size, change all instances of 100-->
                	<xsl:choose>
                    	<xsl:when test="$currentAngle > 3.14">
                        	<xsl:text>1 </xsl:text>
            			</xsl:when>
                        <xsl:otherwise>
                             <xsl:text>0 </xsl:text>
                        </xsl:otherwise>
                   </xsl:choose>
                   <xsl:text>1 </xsl:text>
                   <xsl:value-of select="java:java.lang.Math.cos($currentAngle) * 100"/>
                   <xsl:text> </xsl:text>
                   <xsl:value-of select="java:java.lang.Math.sin($currentAngle) * 100"/>
                   <xsl:text> L 0 0 Z</xsl:text>
               </xsl:attribute>
        </svg:path>
		<!--svg:text style="text-anchor:middle">
	          <xsl:attribute name="transform">
	               <xsl:text>translate(150,150) </xsl:text> 
	          </xsl:attribute>
	          <xsl:attribute name="x">
	               <xsl:value-of select="($x1 * $cosTheta) - ($y1 * $sinTheta)"/>
	          </xsl:attribute>
	          <xsl:attribute name="y">
	               <xsl:value-of select="($x1 * $sinTheta) + ($y1 * $cosTheta)"/>
	          </xsl:attribute>
	          <xsl:value-of select="round(100 * ($number div $total))"/>
	          <xsl:text>%</xsl:text>
	    </svg:text-->
	</xsl:template>
	<xsl:template match="pieslice" mode="legend">
	     <xsl:param name="color" select="'indianRed'"/>
	     <xsl:param name="offset" select="'0'"/>
	     <svg:text>
	          <xsl:attribute name="style">
	               <xsl:text>font-size:12; text-anchor:start</xsl:text>
	          </xsl:attribute>
	          <xsl:attribute name="x">
	               <xsl:text>320</xsl:text>
	          </xsl:attribute>
	          <xsl:attribute name="y">
	               <xsl:value-of select="$offset"/>
	          </xsl:attribute>
	          <xsl:value-of select="@name"/>
	          <xsl:text> (</xsl:text>
	          <xsl:value-of select="."/>
	          <xsl:text>) </xsl:text>
	     </svg:text>
	     <svg:path>
	          <xsl:attribute name="style">
	               <xsl:text>stroke:black; stroke-width:1; fill:</xsl:text>
	               <xsl:value-of select="$color"/>
	          </xsl:attribute>
	          <xsl:attribute name="d">
	               <xsl:text>M 290 </xsl:text>
	               <xsl:value-of select="$offset - 10"/>
	               <xsl:text> L 290 </xsl:text>
	               <xsl:value-of select="$offset"/>
	               <xsl:text> L 300 </xsl:text>
	               <xsl:value-of select="$offset"/>
	               <xsl:text> L 300 </xsl:text>
	               <xsl:value-of select="$offset - 10"/>
	               <xsl:text> Z</xsl:text>
	          </xsl:attribute>
	     </svg:path>
	</xsl:template>
	
	
	<xsl:template match="*">
	</xsl:template>
	
</xsl:stylesheet>