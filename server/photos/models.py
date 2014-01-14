from django.db import models

class Photo(models.Model):
    title = models.CharField(max_length=255,blank=True)
    photo = models.FileField(upload_to='photos')
    description = models.TextField(blank=True)
    uploaded = models.DateTimeField(auto_now_add=True)
    modified = models.DateTimeField(auto_now=True)
    class Meta:
        db_table = 'media_photos'
    def __unicode__(self):
        return '%s' % self.title
