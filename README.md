# theweekendcafe - Orders

App Functionality:


Authorized users (user's whose info in added in DB by admin) can only login, such a user is refered to Waiter

Waiter can view Menu fetched from DB and easily select item with quantity for a new order

Order summary is shown with all items, quantites, and total amount

Once order is submitted, a JSON object is created with order info and sent to server using REST API

In Home Page, ongoing orders can be viewed and selected to view order summary and edit order by adding more items

Once an order is completed and bill is paid, the waiter can close the order and the order status is updated in the DB
