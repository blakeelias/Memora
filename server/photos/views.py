from django.http import HttpResponse


def index(request):
    return HttpResponse("Hello, world. You're at the polls index.")

def upload_photo(request):
    form=PhotoForm(request.POST,request.FILES)
    if request.method=='POST':
        if form.is_valid():
            image = request.FILES['photo']
            title1 =''
            new_image = Photo(title=title1,photo=image,description='')
            new_image.save()
            response_data=[{"success": "1"}]
            return HttpResponse(simplejson.dumps(response_data), mimetype='application/json')
