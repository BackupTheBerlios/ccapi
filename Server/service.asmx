<%@Page Language="C#" %>

<% // This is executed at page startup

public void Page_Load()
{
  label1.InnerHtml = now(); 
  }
  
  %>
  
  <html>
  <body>
  <h2>Hello Mono!</h2>
  <p><span id="label1" runat=server/></p>
  </body>
  </html>

