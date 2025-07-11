踩坑记录

register.html  qq网页版不显示背景图片
解决方案：
解决办法是 在div的外面加上一层  <table>和<td>
并在<td>里加上background属性，例如
<table>
<tr>
<td  background=”http://www.vivizu.com/test.jpg”>
<div></div>
</td>
</tr>
</table>

谷歌邮箱不支持display的flex样式,如 display: flex; align-items: center; justify-content: center;

7/11
@NotEmpty可以判null和 字符串、集合（Collection）、Map、数组等是否为空。而@NotNull只能判null

