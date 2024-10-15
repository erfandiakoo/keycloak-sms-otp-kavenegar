<div class="alert alert-danger">
    Invalid OTP. Please try again.
</div>
<form action="${url.loginAction}" method="post">
    <div class="form-group">
        <label for="otp">Enter OTP</label>
        <input type="text" id="otp" name="otp" class="form-control" />
    </div>
    <button type="submit" class="btn btn-primary">Submit OTP</button>
</form>