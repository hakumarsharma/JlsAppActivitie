for i in `find . -name "*.py" `
do
pep8  --config=setup.cfg  $i
done
