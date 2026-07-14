$path = 'C:\Users\HP\Devin1\VidyaPrayag\zerow_copy.docx'
Add-Type -AssemblyName 'System.IO.Compression.FileSystem'
$zip = [System.IO.Compression.ZipFile]::OpenRead($path)
$entry = $zip.Entries | Where-Object { $_.FullName -eq 'word/document.xml' }
$stream = $entry.Open()
$reader = New-Object System.IO.StreamReader($stream)
$xml = $reader.ReadToEnd()
$reader.Close()
$zip.Dispose()
$xml = $xml -replace '<w:p[^>]*>', "`n"
$xml = $xml -replace '<[^>]+>', ''
$xml = $xml -replace '&amp;', '&'
$xml = $xml -replace '&lt;', '<'
$xml = $xml -replace '&gt;', '>'
$xml = $xml -replace '&quot;', '"'
$xml = $xml -replace '&apos;', "'"
$xml | Out-File -FilePath 'C:\Users\HP\Devin1\VidyaPrayag\zerow_extracted.txt' -Encoding UTF8
